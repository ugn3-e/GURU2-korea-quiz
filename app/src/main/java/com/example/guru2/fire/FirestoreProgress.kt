package com.example.guru2.fire

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreProgress (

    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    ) {
    private fun uid(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("로그인된 사용자가 없습니다")
    }

    // 이어서 학습 불러오기
    fun loadSpellNextQuizId(
        onResult: (Int) -> Unit,
        onError: () -> Unit
    ) {
        db.collection("users")
            .document(uid())
            .collection("progress")
            .document("spell")
            .get()
            .addOnSuccessListener { doc ->
                val nextQuizId =
                    doc.getLong("nextQuizId")?.toInt() ?: 1
                onResult(nextQuizId)
            }
            .addOnFailureListener {
                onError()
            }
    }

    // 이어서 학습 저장
    fun saveSpellNextQuizId(
        nextQuizId: Int,
        onSuccess: () -> Unit = {},
        onFail: (Exception) -> Unit = {}
    ) {
        val data = hashMapOf(
            "nextQuizId" to nextQuizId,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(uid())
            .collection("progress")
            .document("spell")
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFail(e) }
    }
}
