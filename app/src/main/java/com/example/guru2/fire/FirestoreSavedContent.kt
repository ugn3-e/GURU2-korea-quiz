package com.example.guru2.fire

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class SavedSpellItem(
    val quizId: Int = 0,
    val savedDate: String = "",
    val savedAt: Long = 0L
)

class FirestoreSavedContent (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun uid(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("로그인된 사용자가 없습니다")
    }

    // 저장
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
            .document(quizId.toString())     // quizId를 문서 id로 쓰면 중복 저장 방지됨
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFail(e) }
    }

    // 맞춤법 퀴즈 저장목록
    fun loadSpell(
        onResult: (List<SavedSpellItem>) -> Unit,
        onFail: (Exception) -> Unit = {}
    ) {
        db.collection("users")
            .document(uid())
            .collection("saved")
            .document("spell")
            .collection("items")
            .orderBy("savedAt") // 최신순은 아래에서 reversed 하거나 내림차순 쿼리도 가능
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
