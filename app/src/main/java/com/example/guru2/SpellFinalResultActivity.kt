package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class SpellFinalResultActivity : AppCompatActivity() {

    private lateinit var tvSummary: TextView
    private lateinit var btnKeep: MaterialButton
    private lateinit var btnHome: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spell_final_result)

        // ================= 툴바 =================
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Quiz"

        // ================= View =================
        tvSummary = findViewById(R.id.SpellSummary)
        btnKeep = findViewById(R.id.btnQKeep)
        btnHome = findViewById(R.id.btnHome)

        // ================= 결과 데이터 =================
        val total = intent.getIntExtra("totalSCount", 0)
        val correct = intent.getIntExtra("correctSCount", 0)

        tvSummary.text = """
            수고하셨습니다! 🎉
            
            틀린 문제는
            다시 학습해볼 수 있어요!
        """.trimIndent()

        // ================= 이어서 학습 =================
        // 🔥 slang과 동일: QuizActivity 재진입
        btnKeep.setOnClickListener {
            val intent = Intent(this, SpellQuizActivity::class.java)
            startActivity(intent)
            finish()
        }

        // ================= 홈으로 =================
        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // ================= 메뉴 =================
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                startActivity(Intent(this, SurveyActivity::class.java))
                return true
            }
            R.id.action_mypage -> {
                startActivity(
                    Intent(this, com.example.guru2.mypage.MyPageActivity::class.java)
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
