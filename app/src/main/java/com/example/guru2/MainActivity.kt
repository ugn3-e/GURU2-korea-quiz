package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback


class MainActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var btnQuiz1: Button
    lateinit var btnQuiz2: Button
    lateinit var btnSaved: Button // ✅ '콘텐츠 저장하기' 페이지 연결 (유빈 추가)
    // 디미 유진_오답 버튼
    lateinit var btnWrong: Button

    // 디미 유진_뒤로가기 시간 체크 변수
    private var backPressedTime = 0L



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        //툴바를 액션바로 연결 // ☑️추가
        toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)

        btnQuiz1 = findViewById<Button>(R.id.btnQuiz1)

        btnQuiz1.setOnClickListener {
            val intent = Intent(this, SpellQuizActivity::class.java)
            startActivity(intent)
        }

        btnQuiz2 = findViewById<Button>(R.id.btnQuiz2)

        btnQuiz2.setOnClickListener {
            val intent = Intent(this, SlangQuizActivity::class.java)
            startActivity(intent)
        }

        // ✅ '콘텐츠 저장하기' 페이지 연결 (유빈 추가)
        btnSaved = findViewById<Button>(R.id.btnSaved)
        btnSaved.setOnClickListener {
            val intent = Intent(this, SavedContentActivity::class.java)
            startActivity(intent)
        }

        // 디미 유진_오답 연결
        btnWrong = findViewById(R.id.btnWrong)

        btnWrong.setOnClickListener {
            startActivity(Intent(this, WrongNoteActivity::class.java))
        }

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

}