package com.example.guru2.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.guru2.R
import com.example.guru2.saved.SavedContentActivity
import com.example.guru2.slangquiz.SlangQuizActivity
import com.example.guru2.spellquiz.SpellQuizActivity
import com.example.guru2.survey.SurveyActivity
import com.example.guru2.wrong.WrongNoteActivity
import com.example.guru2.mypage.MyPageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    // 툴바
    lateinit var toolbar: Toolbar
    // 홈 화면 버튼
    lateinit var btnQuiz1: View
    lateinit var btnQuiz2: View
    lateinit var btnSaved: View
    lateinit var btnWrong: View

    // 뒤로가기 시간 체크 변수
    private var backPressedTime = 0L

    private lateinit var levelText: TextView // 레벨 텍스트
    private lateinit var progressBar: ProgressBar // 파란색 게이지
    private lateinit var tvUserName: TextView // 닉네임
    private lateinit var tvCharacterPercent: TextView // 퍼센트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 툴바를 액션바로 연결
        toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기존 AppName 없애기

        // UI 요소 연결
        levelText = findViewById(R.id.tvCharacterLevel)
        progressBar = findViewById(R.id.levelProgressBar)
        tvUserName = findViewById(R.id.tvUserName)
        tvCharacterPercent = findViewById(R.id.tvCharacterPercent)

        // 맞춤법 퀴즈
        btnQuiz1 = findViewById(R.id.btnQuiz1)
        btnQuiz1.findViewById<TextView>(R.id.tvTitle).text = "맞춤법 퀴즈"
        btnQuiz1.findViewById<TextView>(R.id.tvSubTitle).text = "Spelling Quiz"
        btnQuiz1.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_spelling)
        btnQuiz1.setOnClickListener {
            val intent = Intent(this, SpellQuizActivity::class.java)
            startActivity(intent)
        }


        // 신조어 퀴즈
        btnQuiz2 = findViewById(R.id.btnQuiz2)
        btnQuiz2.findViewById<TextView>(R.id.tvTitle).text = "신조어 퀴즈"
        btnQuiz2.findViewById<TextView>(R.id.tvSubTitle).text = "Slang Quiz"
        btnQuiz2.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_slang)
        btnQuiz2.setOnClickListener {
            val intent = Intent(this, SlangQuizActivity::class.java)
            startActivity(intent)
        }


        // 저장한 콘텐츠
        btnSaved = findViewById(R.id.btnSaved)
        btnSaved.findViewById<TextView>(R.id.tvTitle).text = "저장한 콘텐츠"
        btnSaved.findViewById<TextView>(R.id.tvSubTitle).text = "Saved Content"
        btnSaved.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_saved)
        btnSaved.setOnClickListener {
            val intent = Intent(this, SavedContentActivity::class.java)
            startActivity(intent)
        }

        // 오답
        btnWrong = findViewById(R.id.btnWrong)
        btnWrong.findViewById<TextView>(R.id.tvTitle).text = "오답"
        btnWrong.findViewById<TextView>(R.id.tvSubTitle).text = "Incorrect Answers"
        btnWrong.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_incorrect)
        btnWrong.setOnClickListener {
            startActivity(Intent(this, WrongNoteActivity::class.java))
        }

        // 뒤로가기 두 번 누르면 앱 종료
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - backPressedTime < 2000) {
                        // 2초 이내 두 번째 뒤로가기 -> 앱 종료
                        finish()
                    } else {
                        // 첫 번째 뒤로가기
                        backPressedTime = currentTime
                        Toast.makeText(
                            this@MainActivity,
                            "뒤로가기를 한 번 더 누르면 \n앱이 종료됩니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }

    // 메인화면과 메뉴 연결
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 상단 메뉴 클릭
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                val intent = Intent(this, SurveyActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_mypage -> {
                startActivity(Intent(this, MyPageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 로그인 보장
    private fun ensureFirebaseAuth(onReady: () -> Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            onReady()
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener { onReady() }
                .addOnFailureListener { e -> Log.e("AUTH", "anonymous login failed", e) }
        }
    }

    // 화면 돌아올 때마다 레벨 갱신
    override fun onResume() {
        super.onResume()
        loadLevelFromFirestoreAndUpdateUI()
    }

    // Firebase uid 기준으로 레벨/닉네임 로드
    // 레벨 표시
    private fun loadLevelFromFirestoreAndUpdateUI() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        // 로그인 안된 상태면 기본값 표시
        if (uid == null) { // 기본값
            levelText.text = "Lv.1"
            tvUserName.text = "슈니님"
            tvCharacterPercent.text = "0%"
            progressBar.progress = 0
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { snap ->
                val level = snap.getLong("level") ?: 1L
                val totalSolved = snap.getLong("totalSolved") ?: 0L
                val nickname = snap.getString("nickname") ?: "게스트"

                // 닉네임 표시
                tvUserName.text = "${nickname}님"
                // 레벨 표시
                levelText.text = "Lv.$level"

                // 진행률 퍼센트 계산 (50문제=100%)
                val progressPercent = if (totalSolved >= 50) 100 else ((totalSolved / 50.0) * 100).toInt()
                progressBar.setProgress(progressPercent, true)
                tvCharacterPercent.text = "$progressPercent%"

                Log.d("MAIN_LEVEL", "uid=$uid level=$level totalSolved=$totalSolved")
            }
            .addOnFailureListener { e ->
                Log.e("MAIN_LEVEL", "load failed", e)
                // 실패 시 기본 표시
                levelText.text = "Lv.1"
                tvCharacterPercent.text = "0%"
                progressBar.progress = 0
            }
    }

    // 뒤로 가기
    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime < 2000) {
                    finish()
                } else {
                    backPressedTime = currentTime
                    Toast.makeText(this@MainActivity, "뒤로가기를 한 번 더 누르면 앱이 종료됩니다", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}