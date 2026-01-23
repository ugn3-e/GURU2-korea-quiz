package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.util.LevelUtil
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var btnQuiz1: android.view.View
    lateinit var btnQuiz2: android.view.View
    lateinit var btnSaved: android.view.View
    lateinit var btnWrong: android.view.View

    // 디미 유진_뒤로가기 시간 체크 변수
    private var backPressedTime = 0L

    private lateinit var levelText: TextView       // 레벨 텍스트
    private lateinit var progressBar: ProgressBar   // 파란색 게이지
    private lateinit var tvUserName: TextView      // 사용자 이름

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //userId = intent.getIntExtra("user_id", -1).toLong()
        val userLevel = intent.getStringExtra("user_level") ?: "Lv.1"

        // 확인용 토스트 (삭제 예정)
        //Toast.makeText(this, "환영합니다! ID: $userId, 레벨: $userLevel", Toast.LENGTH_SHORT).show()

        // 툴바를 액션바로 연결
        toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 기존 AppName 없애기

        levelText = findViewById(R.id.tvCharacterLevel)
        progressBar = findViewById(R.id.levelProgressBar)
        tvUserName = findViewById(R.id.tvUserName)

        // 맞춤법 퀴즈
        btnQuiz1 = findViewById(R.id.btnQuiz1)
        btnQuiz1.findViewById<TextView>(R.id.tvTitle).text = "맞춤법 퀴즈"
        btnQuiz1.findViewById<TextView>(R.id.tvSubTitle).text = "Spelling Quiz"
        btnQuiz1.findViewById< ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_spelling)
        btnQuiz1.setOnClickListener {
            val intent = Intent(this, SpellQuizActivity::class.java)
            intent.putExtra("quiz_id", -1) // 이어하기 값 전달
            intent.putExtra("quiz_count", 1)
            startActivity(intent)
        }

        // 신조어 퀴즈
        btnQuiz2 = findViewById(R.id.btnQuiz2)
        btnQuiz2.findViewById<TextView>(R.id.tvTitle).text = "신조어 퀴즈"
        btnQuiz2.findViewById<TextView>(R.id.tvSubTitle).text = "Slang Quiz"
        btnQuiz2.findViewById< ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_slang)
        btnQuiz2.setOnClickListener {
            val intent = Intent(this, SlangQuizActivity::class.java)
            //intent.putExtra("user_id", userId)  // 디미 유진_유저 아이디 값 넘김
            startActivity(intent)
        }

        // 저장
        btnSaved = findViewById(R.id.btnSaved)
        btnSaved.findViewById<TextView>(R.id.tvTitle).text = "저장한 콘텐츠"
        btnSaved.findViewById<TextView>(R.id.tvSubTitle).text = "Saved Content"
        btnSaved.findViewById< ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_saved)
        btnSaved.setOnClickListener {
            val intent = Intent(this, SavedContentActivity::class.java)
            startActivity(intent)
        }

        // 오답
        btnWrong = findViewById(R.id.btnWrong)
        btnWrong.findViewById<TextView>(R.id.tvTitle).text = "오답"
        btnWrong.findViewById<TextView>(R.id.tvSubTitle).text = "Incorrect Answers"
        btnWrong.findViewById< ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_incorrect)
        btnWrong.setOnClickListener {
            startActivity(Intent(this, WrongNoteActivity::class.java))
        }

        // 디미 유진_레벨 뷰 연결 (유빈 추가 수정 level -> tvCharacterLevel)
        levelText= findViewById(R.id.tvCharacterLevel)

        // 디미 유진_뒤로가기 두 번 누르면 앱 종료
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - backPressedTime < 2000) {
                        // 디미 유진_2초 이내 두 번째 뒤로가기 -> 앱 종료
                        finish()

                        // 디미 유진_앱 완전 종료
                        //finishAffinity()
                    } else {
                        // 디미 유진_첫 번째 뒤로가기
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

    // 디미 유진_(추가)마이페이지 추가
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

    // 디미 유진_화면 돌아올 때마다 레벨 갱신
    override fun onResume() {
        super.onResume()
        loadLevelFromFirestoreAndUpdateUI() // 🔥 수정: Firebase 기준 통일
    }

    // 🔥 Firebase uid 기준으로 메인 레벨/닉네임 로드

    // 레벨 표시 ☑️
    private fun loadLevelFromFirestoreAndUpdateUI() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            levelText.text = "Lv.1"
            tvUserName.text = "게스트님"
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

                // 디미 유진_닉네임 표시
                tvUserName.text = "${nickname}님"
                // ✅ 레벨 표시
                levelText.text = "Lv.$level"

                // ✅ 게이지 표시(너가 쓰던 방식 그대로: 60문제=100%)
                val progressPercent = if (totalSolved >= 60) 100 else ((totalSolved / 60.0) * 100).toInt()
                progressBar.setProgress(progressPercent, true)

                Log.d("MAIN_LEVEL", "uid=$uid level=$level totalSolved=$totalSolved")
            }
            .addOnFailureListener { e ->
                Log.e("MAIN_LEVEL", "load failed", e)
                // 실패 시 기본 표시
                levelText.text = "Lv.1"
                progressBar.progress = 0
            }
    }

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