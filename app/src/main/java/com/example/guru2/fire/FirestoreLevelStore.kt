package com.example.guru2.fire

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

// "레벨, 총 푼 문제 수"를 저장/불러오기
class FirestoreLevelStore (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // 현재 로그인된 유저 uid 가져오기
    private fun uid(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("로그인된 사용자가 없습니다")
    }

    // totalSolved 기반으로 level 계산
    fun addSolved1(
        onSuccess: (level: Long, totalSolved: Long) -> Unit = { _, _ -> },
        onFail: (Exception) -> Unit = {}
    ) {
        val docRef = db.collection("users").document(uid())

        db.runTransaction { tx ->
            val snap = tx.get(docRef)

            // 기본 값 없으면 0으로 시작해서 +1
            val totalSolved = (snap.getLong("totalSolved") ?: 0L) + 1L
            // 레벨 계산
            val level = (totalSolved / 10L) + 1L

            tx.set(
                docRef,
                mapOf(
                    "totalSolved" to totalSolved,
                    "level" to level,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            level to totalSolved
        }.addOnSuccessListener { (level, totalSolved) ->
            onSuccess(level, totalSolved)
        }.addOnFailureListener { e ->
            onFail(e)
        }
    }

    // 현재 레벨 불러오기
    fun loadLevel(
        onSuccess: (level: Long) -> Unit,
        onFail: (Exception) -> Unit = {}
    ) {
        db.collection("users")
            .document(uid())
            .get()
            .addOnSuccessListener { snap ->
                onSuccess(snap.getLong("level") ?: 1L)
            }
            .addOnFailureListener { e -> onFail(e) }
    }
}