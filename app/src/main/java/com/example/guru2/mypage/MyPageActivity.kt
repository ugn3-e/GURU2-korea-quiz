package com.example.guru2.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.MainActivity
import com.example.guru2.R
import com.example.guru2.login.LoginActivity
import com.example.guru2.util.LevelUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPageActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page2)

        // ===== View 연결 =====
        val tvNickname = findViewById<TextView>(R.id.tvNickname)
        val tvNotice = findViewById<TextView>(R.id.tvNotice)
        val tvLevel = findViewById<TextView>(R.id.tvLevel)
        val tvProgress = findViewById<TextView>(R.id.tvProgress)
        val progressLevel = findViewById<ProgressBar>(R.id.progressLevel)

        val btnHome = findViewById<Button>(R.id.btnHome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)

        // ===== 기본 안내 문구 =====
        tvNotice.text =
            "꾸준한 학습 기록이 레벨을 유지합니다.\nKoready가 당신의 한국어 학습을 응원합니다!"

        // ===== 현재 로그인 유저 =====
        val uid = auth.currentUser?.uid
        if (uid == null) {
            goLogin(clearTask = true)
            return
        }

        // ===== Firestore에서 사용자 정보 로드 =====
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    tvNickname.text = "-"
                    tvLevel.text = "Lv.1"
                    tvProgress.text = "0%"
                    progressLevel.progress = 0
                    return@addOnSuccessListener
                }

                val nickname = doc.getString("nickname") ?: "-"
                val solvedCount = doc.getLong("solved_count")?.toInt() ?: 0

                val level = LevelUtil.calculateLevel(solvedCount)
                val progressPercent = (solvedCount % 10) * 10

                // ===== UI 반영 =====
                tvNickname.text = nickname
                tvLevel.text = "Lv.$level"
                tvProgress.text = "$progressPercent%"
                progressLevel.progress = progressPercent
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "마이페이지 정보를 불러오지 못했습니다.\n${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // ===== 버튼 이벤트 =====

        // 홈으로 이동
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        // 로그아웃
        btnLogout.setOnClickListener {
            auth.signOut()
            getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply()
            goLogin(clearTask = true)
        }

        // 프로필 정보 수정 (아직 화면 없으면 토스트)
        btnEditProfile.setOnClickListener {
            Toast.makeText(this, "프로필 수정은 준비 중입니다.", Toast.LENGTH_SHORT).show()
            // TODO: EditProfileActivity 연결
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
