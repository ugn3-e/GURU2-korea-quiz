package com.example.guru2.fire

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// "이어서 학습" 진행상황 저장/불러오기
class FirestoreProgress (

    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    ) {
    // 현재 로그인된 유저 uid 가져오기
    private fun uid(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("로그인된 사용자가 없습니다")
    }

    // 맞춤법 퀴즈_이어서 학습 불러오기(nextQuizId)
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

    // 맞춤법 퀴즈_이어서 학습 저장(nextQuizId)
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

    // 신조어 퀴즈_이어서 학습 불러오기(nextQuizId)
    fun loadSlangNextQuizId(
        onResult: (Int) -> Unit,
        onError: () -> Unit
    ) {
        db.collection("users")
            .document(uid())
            .collection("progress")
            .document("slang")
            .get()
            .addOnSuccessListener { doc ->
                val nextQuizId = doc.getLong("nextQuizId")?.toInt() ?: 1
                onResult(nextQuizId)
            }
            .addOnFailureListener {
                onError()
            }
    }

    // 신조어 퀴즈_이어서 학습 저장(nextQuizId)
    fun saveSlangNextQuizId(nextQuizId: Int) {
        val data = hashMapOf(
            "nextQuizId" to nextQuizId,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(uid())
            .collection("progress")
            .document("slang")
            .set(data)
    }
}
