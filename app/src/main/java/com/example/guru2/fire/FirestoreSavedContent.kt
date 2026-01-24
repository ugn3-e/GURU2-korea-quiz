package com.example.guru2.fire

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// "저장한 콘텐츠" 저장/불러오기
data class SavedSpellItem(
    val quizId: Int = 0,
    val savedDate: String = "",
    val savedAt: Long = 0L
)

class FirestoreSavedContent (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // 현재 로그인된 유저 uid 불러오기
    private fun uid(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("로그인된 사용자가 없습니다")
    }

    // 맞춤법 퀴즈 저장
    // quizId → 중복 저장 방지
    fun saveSpell(
        quizId: Int, savedDate: String,
        onSuccess: () -> Unit = {},
        onFail: (Exception) -> Unit = {}
    ) {

        val data = hashMapOf(
            "quizId" to quizId,
            "savedDate" to savedDate,
            "savedAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(uid())
            .collection("saved")
            .document("spell")
            .collection("items")
            .document(quizId.toString()) // 중복 저장 방지
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFail(e) }
    }

    // 맞춤법 퀴즈 저장 목록 불러오기
    fun loadSpell(
        onResult: (List<SavedSpellItem>) -> Unit,
        onFail: (Exception) -> Unit = {}
    ) {
        db.collection("users")
            .document(uid())
            .collection("saved")
            .document("spell")
            .collection("items")
            .orderBy("savedAt")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { doc ->
                    val quizId = doc.getLong("quizId")?.toInt() ?: return@mapNotNull null
                    val savedDate = doc.getString("savedDate") ?: ""
                    val savedAt = doc.getLong("savedAt") ?: 0L
                    SavedSpellItem(quizId, savedDate, savedAt)
                }
                onResult(list)
            }
            .addOnFailureListener { e -> onFail(e) }
    }
}
