package com.example.guru2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SpellFinalResultActivity : AppCompatActivity() {
    lateinit var sSummary: TextView
    lateinit var btnQKeep: Button
    lateinit var btnHome: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spell_final_result)

        val total = intent.getIntExtra("totalSCount", 0)
        val correct = intent.getIntExtra("correctSCount", 0)
        val setCorrect = intent.getIntExtra("setCorrectCount", 0)

        val nextQuizId = intent.getIntExtra("next_quiz_id", -1)
        val nextQuizCount = intent.getIntExtra("next_quiz_count", 1)

        // 결과 요약 텍스트
        sSummary = findViewById<TextView>(R.id.SpellSummary)

        // 이어서 학습 버튼
        btnQKeep = findViewById<Button>(R.id.btnQKeep)

        // 홈 버튼
        btnHome = findViewById<Button>(R.id.btnHome)

        sSummary.text = """
            🎉 오늘의 모든 퀴즈를 완료했습니다!
            수고하였습니다!
            
            총 문제 수 : 5
            맞힌 문제 수 : $setCorrect
        """.trimIndent()

        btnQKeep.setOnClickListener {
            val intent = Intent(this, SpellQuizActivity::class.java)

            intent.putExtra("quiz_id", nextQuizId) // QuizActivity1에서 퀴즈 카운트는 1부터 시작
            intent.putExtra("quiz_count", nextQuizCount) // 마지막 위치 전달

            intent.putExtra("setCorrectCount", 0) // 5문제만 계산

            intent.putExtra("totalSCount", total)
            intent.putExtra("correctSCount", correct)

            startActivity(intent)
            finish()
        }

        btnHome.setOnClickListener {
            // 홈 버튼 클릭
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}