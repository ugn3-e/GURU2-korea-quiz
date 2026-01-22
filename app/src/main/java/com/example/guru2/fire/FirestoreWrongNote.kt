package com.example.guru2.fire

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class WrongRecord(
    val quizId: Int = 0,
    val userAnswer: String = "",
    val wrongAt: Long = 0L,
    val wrongDate: String = ""
)

class FirestoreWrongNote(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
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
            .document(type)              // "spell" / "slang"
            .collection("records")
            .add(data)                   // 여러 번 틀린 기록을 남기고 싶으면 add()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFail(e) }
    }

    // 오답 불러오기 (최근순)
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

    // 전체 초기화 (type 한 종류만 지움)
    fun clearAll(
        type: String,
        onSuccess: () -> Unit = {},
        onFail: (Exception) -> Unit = {}
    ) {
        db.collection("users")
            .document(uid())
            .collection("wrong")
            .document(type)
            .collection("records")
            .get()
            .addOnSuccessListener { snap ->
                val batch = db.batch()
                snap.documents.forEach { batch.delete(it.reference) }
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onFail(e) }
            }
            .addOnFailureListener { e -> onFail(e) }
    }
}