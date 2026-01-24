package com.example.guru2.fire

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class WrongRecord(
    val quizId: Int = 0,
    val userAnswer: String = "",
    val wrongAt: Long = 0L,
    val wrongDate: String = ""
)
// "오답" 저장/불러오기
class FirestoreWrongNote(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // 현재 로그인된 유저 uid 불러오기
    private fun uid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("로그인된 사용자가 없습니다")

    private fun todayDateString(millis: Long): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.KOREA)
        return sdf.format(java.util.Date(millis))
    }

    // 오답 저장 (type = "spell" or "slang")
    fun addWrong(
        type: String,
        quizId: Int,
        userAnswer: String,
        onSuccess: () -> Unit = {},
        onFail: (Exception) -> Unit = {}
    ) {
        val now = System.currentTimeMillis()
        val data = hashMapOf(
            "quizId" to quizId,
            "userAnswer" to userAnswer,
            "wrongAt" to now,
            "wrongDate" to todayDateString(now)
        )

        db.collection("users")
            .document(uid())
            .collection("wrong")
            .document(type) // "spell" or "slang"
            .collection("records")
            .add(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFail(e) }
    }

    // 오답 불러오기 (최신순)
    fun loadWrongs(
        type: String,
        onResult: (List<WrongRecord>) -> Unit,
        onFail: (Exception) -> Unit = {}
    ) {
        db.collection("users")
            .document(uid())
            .collection("wrong")
            .document(type)
            .collection("records")
            .orderBy("wrongAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { doc ->
                    val quizId = doc.getLong("quizId")?.toInt() ?: return@mapNotNull null
                    val userAnswer = doc.getString("userAnswer") ?: ""
                    val wrongAt = doc.getLong("wrongAt") ?: 0L
                    val wrongDate = doc.getString("wrongDate") ?: ""
                    WrongRecord(quizId, userAnswer, wrongAt, wrongDate)
                }
                onResult(list)
            }
            .addOnFailureListener { e -> onFail(e) }
    }
}