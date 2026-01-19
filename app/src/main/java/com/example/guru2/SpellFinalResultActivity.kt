package com.example.guru2

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // 결과 요약 텍스트
        sSummary = findViewById<TextView>(R.id.SpellSummary)

        // 이어서 학습 버튼
        btnQKeep = findViewById<Button>(R.id.btnQKeep)

        // 홈 버튼
        btnHome = findViewById<Button>(R.id.btnHome)

        sSummary.text = """
            🎉 오늘의 모든 퀴즈를 완료했습니다!
            수고하였습니다!
            
            총 문제 수 : $total
            맞힌 문제 수 : $correct
        """.trimIndent()

        btnQKeep.setOnClickListener {
            //이어서 학습하기 기능 구현 필요
            Toast.makeText(this, "이어서 학습하기 버튼", Toast.LENGTH_SHORT).show()
        }

        btnHome.setOnClickListener {
            finish()
        }

    }
}