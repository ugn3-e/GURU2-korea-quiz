package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.OnBackPressedCallback


class SlangFinalResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slang_final_result)

        // 디미 유진_결과 요약 텍스트
        val tvSummary = findViewById<TextView>(R.id.tvSummary)

        // 디미 유진_이어서 학습하기 버튼
        val btnKeep = findViewById<Button>(R.id.btnKeep)

        // 디미 유진_홈으로 돌아가기 버튼
        val btnFinish = findViewById<Button>(R.id.btnFinish)

        val total = intent.getIntExtra("totalCount", 0)
        val correct = intent.getIntExtra("correctCount", 0)

        // 디미 유진_퀴즈 결과 요약 메세지
        tvSummary.text = """
            🎉 오늘의 모든 퀴즈를 완료했습니다!
            수고하였습니다!
            
            총 문제 수 : $total
            맞힌 문제 수 : $correct
        """.trimIndent()

        // 디미 유진_이어서 학습하기 버튼
        btnKeep.setOnClickListener {
            val pref = getSharedPreferences("slang_quiz", MODE_PRIVATE)
            val lastQuizId = pref.getInt("lastQuizId", 0)

            val intent = Intent(this, SlangQuizActivity::class.java).apply {
                putExtra("startQuizId", lastQuizId + 1)
            }

            startActivity(intent)
            finish()
        }

        // 디미 유진_(수정) MainActivity로 이동 (뒤로 가기 눌러도 이전 퀴즈 화면으로 돌아가지 않음)
        btnFinish.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()

        }

        // 디미 유진_뒤로가기 막기
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(
                        this@SlangFinalResultActivity,
                        "이어 학습하기 버튼이나 \n홈 버튼을 눌러주세요!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }
}