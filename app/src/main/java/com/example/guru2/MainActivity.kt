package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.util.LevelUtil


class MainActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var btnQuiz1: android.view.View
    lateinit var btnQuiz2: android.view.View
    lateinit var btnSaved: android.view.View
    lateinit var btnWrong: android.view.View
    // 디미 유진_뒤로가기 시간 체크 변수
    private var backPressedTime = 0L

    // 디미 유진_레벨 텍스트
    private lateinit var levelText: TextView

    // 디미 유진_유저 아이디
    private var userId: Long = -1L



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val userId = intent.getIntExtra("user_id", -1)
        val userLevel = intent.getStringExtra("user_level") ?: "Lv.1"

// 확인용 토스트
        Toast.makeText(this, "환영합니다! ID: $userId, 레벨: $userLevel", Toast.LENGTH_SHORT).show()

        //툴바를 액션바로 연결 // ☑️추가
        toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        // 맞춤법 퀴즈
        btnQuiz1 = findViewById(R.id.btnQuiz1)
        btnQuiz1.findViewById<TextView>(R.id.tvTitle).text = "맞춤법 퀴즈"
        btnQuiz1.findViewById<TextView>(R.id.tvSubTitle).text = "Spelling Quiz"
        btnQuiz1.findViewById< ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_spelling)
        btnQuiz1.setOnClickListener {
            val intent = Intent(this, SpellQuizActivity::class.java)
            intent.putExtra("user_id", userId)  // 디미 유진_유저 아이디 값 넘김
            startActivity(intent)
        }

        // 신조어 퀴즈
        btnQuiz2 = findViewById(R.id.btnQuiz2)
        btnQuiz2.findViewById<TextView>(R.id.tvTitle).text = "신조어 퀴즈"
        btnQuiz2.findViewById<TextView>(R.id.tvSubTitle).text = "Slang Quiz"
        btnQuiz2.findViewById< ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_slang)
        btnQuiz2.setOnClickListener {
            val intent = Intent(this, SlangQuizActivity::class.java)
            intent.putExtra("user_id", userId)  // 디미 유진_유저 아이디 값 넘김
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

        // 디미 유진_레벨 뷰 연결
        levelText= findViewById(R.id.level)

        // 디미 유진_유저 아이디
        //userId = intent.getLongExtra("user_id", -1L)

        // 디미 유진_레벨 출력
        //updateLevelUI()

//        if (userId != -1L) {
//            val repo = AuthRepository(SQLiteAuthDataSource(this))
//            val solvedCount = repo.getSolvedCount(userId)
//
//            val levelValue = LevelUtil.calculateLevel(solvedCount)
//
//            level.text = "Lv.$levelValue · 푼 문제 $solvedCount"
//        }

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

    // 메인화면과 메뉴 연결 //☑️추가
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
        }
        return super.onOptionsItemSelected(item)
    }

    // 디미 유진_레벨 출력
//    private fun updateLevelUI() {
//        if (userId == -1L) return
//
//        val repo = AuthRepository(SQLiteAuthDataSource(this))
//        val solvedCount = repo.getSolvedCount(userId)
//        val levelValue = LevelUtil.calculateLevel(solvedCount)
//
//        level.text = "Lv.$levelValue · 푼 문제 $solvedCount"
//    }

    // 디미 유진_화면 돌아올 때마다 레벨 갱신
    override fun onResume() {
        super.onResume()
        loadUserAndUpdateLevel()
    }

    private fun loadUserAndUpdateLevel() {
        val pref = getSharedPreferences("auth", MODE_PRIVATE)
        userId = pref.getLong("user_id", -1L)

        if (userId == -1L) {
            levelText.text = "Lv.1 · 푼 문제 0"
            return
        }

        val repo = AuthRepository(SQLiteAuthDataSource(this))
        val solvedCount = repo.getSolvedCount(userId)
        val level = LevelUtil.calculateLevel(solvedCount)

        levelText.text = "Lv.$level · 푼 문제 $solvedCount"
    }





}