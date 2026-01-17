package com.example.guru2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class ResultActivity1 : AppCompatActivity() {
    lateinit var DBManager: DBManager
    lateinit var resultText: TextView
    lateinit var quizText: TextView
    lateinit var quizAnswer: TextView
    lateinit var btnNext: Button

    lateinit var btnSave: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ui 연결
        resultText = findViewById<TextView>(R.id.ResultText)
        quizText = findViewById<TextView>(R.id.QuizText)
        quizAnswer = findViewById<TextView>(R.id.QuizAnswer)
        btnNext = findViewById<Button>(R.id.btnNext)
        btnSave = findViewById<Button>(R.id.btnSave)


        // DBManager
        val dbManager = DBManager(this, "quiz.db", null, 1)

        // 데이터 가져오기
        val quiz = dbManager.getQuizById(1)

        val isCorrect = intent.getBooleanExtra("is_correct", false) // ☑️ 추가


        // 더미데이터 넣기
        //isCorrect if문 // ☑️ 추가
        quiz?.let {
            quizText.text = it.sentence.replace("____", it.correct)
            if(isCorrect) {
                resultText.text = "정답!"
                quizAnswer.text = it.correct_exp
            } else {
                resultText.text = "오답"
                quizAnswer.text = it.incorrect_exp
            }


        }

        // 다음 문제 넘어가기
        btnNext.setOnClickListener {
            // 다음 페이지로 이동 // ☑️추가
            startActivity(Intent(this, QuizActivity1::class.java))
            finish() // 뒤로 가기 누를 시 퀴즈 화면

        }

        // 콘텐츠 저장
        btnSave.setOnClickListener {
            // 콘텐츠 저장
        }
    }
}