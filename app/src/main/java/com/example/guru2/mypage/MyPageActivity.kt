package com.example.guru2.mypage

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.MainActivity
import com.example.guru2.R
import com.example.guru2.SurveyActivity
import com.example.guru2.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPageActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page2)

        // 툴바
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // ===== View 연결 =====
        val tvNickname = findViewById<TextView>(R.id.tvNickname)
        val tvNotice = findViewById<TextView>(R.id.tvNotice)
        val tvLevel = findViewById<TextView>(R.id.tvLevel)
        val tvProgress = findViewById<TextView>(R.id.tvProgress)
        val progressLevel = findViewById<ProgressBar>(R.id.progressLevel)

        val btnHome = findViewById<Button>(R.id.btnHome)
        val cardEditProfile = findViewById<View>(R.id.cardEditProfile)
        val btnLogout = findViewById<TextView>(R.id.btnLogout)

        // ===== 기본 안내 문구 =====
        tvNotice.text =
            "꾸준한 학습 기록이 레벨을 유지합니다.\nKoready가 당신의 한국어 학습을 응원합니다!"

        // ===== 로그인 유저 확인 =====
        val uid = auth.currentUser?.uid
        if (uid == null) {
            goLogin(clearTask = true)
            return
        }

        // =====================================================
        // ✅ Firestore → 메인과 동일한 레벨 / 게이지 로직
        // =====================================================
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    tvNickname.text = "-"
                    tvLevel.text = "Lv.1"
                    tvProgress.text = "0%"
                    progressLevel.progress = 0
                    return@addOnSuccessListener
                }

                val nickname = doc.getString("nickname") ?: "사용자"

                // ✅ [메인 기준 적용]
                val level = doc.getLong("level") ?: 1L
                val totalSolved = doc.getLong("totalSolved") ?: 0L

                val progressPercent =
                    if (totalSolved >= 60) 100
                    else ((totalSolved / 60.0) * 100).toInt()

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

        // 프로필 수정
        cardEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
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

    // 메인화면과 메뉴 연결
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                val intent = Intent(this, SurveyActivity::class.java)
                startActivity(intent)
                return true
            }

            // 디미 유진_마이페이지
            R.id.action_mypage -> {
                startActivity(Intent(this, com.example.guru2.mypage.MyPageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
