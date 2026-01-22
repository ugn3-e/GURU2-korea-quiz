package com.example.guru2.fire

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreLevelStore (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun uid(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("로그인된 사용자가 없습니다")
    }

    fun addSolved1(
        onSuccess: (level: Long, totalSolved: Long) -> Unit = { _, _ -> },
        onFail: (Exception) -> Unit = {}
    ) {
        val docRef = db.collection("users").document(uid())

        db.runTransaction { tx ->
            val snap = tx.get(docRef)

            val totalSolved = (snap.getLong("totalSolved") ?: 0L) + 1L
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