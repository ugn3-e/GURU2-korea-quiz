package com.example.guru2.mypage

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.home.MainActivity
import com.example.guru2.R
import com.example.guru2.survey.SurveyActivity
import com.example.guru2.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 마이페이지 화면
class MyPageActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    lateinit var toolbar: Toolbar

    // onResume에서 사용하기 위해 멤버 변수로 선언
    lateinit var tvNickname: TextView
    lateinit var tvNotice: TextView
    lateinit var tvLevel: TextView
    lateinit var tvProgress: TextView
    lateinit var progressLevel: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page2)

        // 툴바
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // View 연결
        tvNickname = findViewById<TextView>(R.id.tvNickname)
        tvNotice = findViewById<TextView>(R.id.tvNotice)
        tvLevel = findViewById<TextView>(R.id.tvLevel)
        tvProgress = findViewById<TextView>(R.id.tvProgress)
        progressLevel = findViewById<ProgressBar>(R.id.progressLevel)

        val btnHome = findViewById<Button>(R.id.btnHome)
        val cardEditProfile = findViewById<View>(R.id.cardEditProfile)
        val btnLogout = findViewById<TextView>(R.id.btnLogout)

        // 기본 안내 문구
        tvNotice.text =
            "꾸준한 학습 기록이 레벨을 유지합니다.\nKoready가 당신의 한국어 학습을 응원합니다!"

        // 로그인 유저 확인
        val uid = auth.currentUser?.uid
        if (uid == null) {
            goLogin(clearTask = true)
            return
        }

        // 최초 진입 시 데이터 로드
        loadMyPageData()

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

        // 프로필 수정 화면으로 이동
        cardEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }

    // 프로필 수정 후 돌아올 때도 최신 정보 반영
    override fun onResume() {
        super.onResume()
        loadMyPageData()
    }

    // Firestore → 메인과 동일한 레벨 / 게이지 계산
    private fun loadMyPageData() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    // 기본값
                    tvNickname.text = "-"
                    tvLevel.text = "Lv.1"
                    tvProgress.text = "0%"
                    progressLevel.progress = 0
                    return@addOnSuccessListener
                }

                val nickname = doc.getString("nickname") ?: "사용자"

                // 레벨, 푼 문제 수(누적)
                val level = doc.getLong("level") ?: 1L
                val totalSolved = doc.getLong("totalSolved") ?: 0L

                // 진행률 계산 (50문제 = 100%)
                val progressPercent =
                    if (totalSolved >= 50) 100
                    else ((totalSolved / 50.0) * 100).toInt()

                // UI 반영
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
    }

    // 로그인 화면으로 이동
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

    // 메뉴 클릭
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                val intent = Intent(this, SurveyActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
