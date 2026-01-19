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
        val dbManager = DBManager(this)

        // 데이터 가져오기
        // quizId, isCorrect
        // 문제 순서, 정답/오답 여부 판단
        val quizId = intent.getIntExtra("quiz_id", -1)
        val isCorrect = intent.getBooleanExtra("is_correct", false)
        val quiz = dbManager.getQuizById(quizId)
        val quizCount = intent.getIntExtra("quiz_count", 1)

        // 누적값 다시 전달 // ☑️ 추가
        val totalSCount = intent.getIntExtra("totalSCount", 0)
        val correctSCount = intent.getIntExtra("correctSCount", 0)

        // 더미데이터 넣기
        //isCorrect if문
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

        // 다음 문제로 이동
        btnNext.setOnClickListener {
            val intent = Intent(this, QuizActivity1::class.java)

            // 다음 문제로 이동
            // startActivity(Intent(this, QuizActivity1::class.java))
            intent.putExtra("quiz_count", quizCount + 1)

            // 누적값 다시 전달 // ☑️ 추가
            intent.putExtra("totalSCount", totalSCount)
            intent.putExtra("correctSCount", correctSCount)

            startActivity(intent)
            finish() // 뒤로 가기 누를 시 퀴즈 화면

        }

        // 콘텐츠 저장
        btnSave.setOnClickListener {
            // 콘텐츠 저장
        }
    }
}