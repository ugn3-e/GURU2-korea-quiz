package com.example.guru2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SpellFinalResultActivity : AppCompatActivity() {
    lateinit var sSummary: TextView
    lateinit var btnQKeep: MaterialButton
    lateinit var btnHome: MaterialButton

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

        // 이어서 학습, 홈 버튼
        // 🔥 [수정] MaterialButton으로 findViewById
        btnQKeep = findViewById(R.id.btnQKeep)
        btnHome = findViewById(R.id.btnHome)

        sSummary.text = """
            수고하셨습니다!
            
            틀린 문제는 복습하기로
            한 번 더 학습할 수 있습니다!
        """.trimIndent()

        btnQKeep.setOnClickListener {

            // DB에 nextQuizId 문제가 실제로 존재하는지 확인
            val spellDb = SpellDBManager(this)
            val nextQuiz = spellDb.getQuizById(nextQuizId)

            // 모든 학습 완료
            if (nextQuiz == null) {
                Toast.makeText(this, "모든 학습을 완료하였습니다!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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