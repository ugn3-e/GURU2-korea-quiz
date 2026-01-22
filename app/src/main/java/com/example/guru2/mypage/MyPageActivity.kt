package com.example.guru2.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.login.LoginActivity
import com.example.guru2.util.LevelUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPageActivity : AppCompatActivity() {
    //private var userId: Long = -1L
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page2)

        val tvNickname = findViewById<TextView>(R.id.tvNickname)
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        val tvLevel = findViewById<TextView>(R.id.tvLevel)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // 현재 로그인 유저 uid 가져오기
        val uid = auth.currentUser?.uid
        if (uid == null) {
            // 로그인 상태가 아니면 로그인 화면으로
            goLogin(clearTask = true)
            return
        }

        // Firestore에서 users/{uid} 문서 읽기
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    tvNickname.text = "-"
                    tvInfo.text = "-"
                    tvLevel.text = "Lv.0 · 푼 문제 0"
                    return@addOnSuccessListener
                }

                val nickname = doc.getString("nickname") ?: "-"
                val gender = doc.getString("gender") ?: "-"
                val ageAny = doc.get("age")
                val age = when (ageAny) {
                    is Long -> ageAny.toInt()
                    is String -> ageAny.toIntOrNull() ?: 0
                    else -> 0
                }
                val country = doc.getString("country") ?: "-"
                val solvedCount = doc.getLong("solved_count")?.toInt() ?: 0

                val level = LevelUtil.calculateLevel(solvedCount)

                tvNickname.text = nickname
                tvInfo.text = "$gender · ${age}세 · $country"
                tvLevel.text = "Lv.$level · 푼 문제 $solvedCount"
            }
            .addOnFailureListener {
                Toast.makeText(this, "불러오기 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        btnLogout.setOnClickListener {
            auth.signOut()

            // prefs는 선택인데, 남아있으면 혼선 생길 수 있어서 같이 비우는 걸 추천
            getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply()

            goLogin(clearTask = true)
        }
    }

    private fun goLogin(clearTask: Boolean = false) {
        val intent = Intent(this, LoginActivity::class.java)
        if (clearTask) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
