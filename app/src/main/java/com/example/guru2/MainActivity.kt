package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guru2.ui.quiz.slang.SlangQuizActivity

class MainActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var btnQuiz1: Button
    lateinit var btnQuiz2: Button
    // 디미 유진_오답 버튼
    lateinit var btnWrong: Button

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
            val intent = Intent(this, QuizActivity1::class.java)
            startActivity(intent)
        }

        // 디미 유진_신조어 퀴즈 연결
        btnQuiz2 = findViewById<Button>(R.id.btnQuiz2)
        btnQuiz2.setOnClickListener {
            val intent = Intent(this, SlangQuizActivity::class.java)
            startActivity(intent)
        }
        // 디미 유진_오답 연결
        btnWrong = findViewById(R.id.btnWrong)
        btnWrong.setOnClickListener {
            startActivity(Intent(this, WrongNoteActivity::class.java))
        }
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