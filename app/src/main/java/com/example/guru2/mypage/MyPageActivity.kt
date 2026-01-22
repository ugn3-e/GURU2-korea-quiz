package com.example.guru2.mypage

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.util.LevelUtil

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page2)

        // ===== View 연결 =====
        val tvNickname = findViewById<TextView>(R.id.tvNickname)
        val tvNotice = findViewById<TextView>(R.id.tvNotice)
        val tvLevel = findViewById<TextView>(R.id.tvLevel)
        val tvProgress = findViewById<TextView>(R.id.tvProgress)
        val progressLevel = findViewById<ProgressBar>(R.id.progressLevel)
        val btnHome = findViewById<Button>(R.id.btnHome)

        // ===== 로그인 유저 =====
        val pref = getSharedPreferences("auth", MODE_PRIVATE)
        val userId = pref.getLong("user_id", -1L)

        if (userId != -1L) {
            val repo = AuthRepository(SQLiteAuthDataSource(this))
            val solvedCount = repo.getSolvedCount(userId)

            val level = LevelUtil.calculateLevel(solvedCount)
            val progressPercent = (solvedCount % 10) * 10   // 예: 76%

            // ===== UI 반영 =====
            tvNickname.text = "김수니"   // ← 실제 닉네임 DB에서 가져오면 교체
            tvLevel.text = "Lv.$level"
            tvProgress.text = "$progressPercent%"
            progressLevel.progress = progressPercent

            tvNotice.text =
                "꾸준한 학습 기록이 레벨을 유지합니다.\nKoready가 당신의 한국어 학습을 응원합니다!"
        }

        // ===== 홈으로 이동 =====
        btnHome.setOnClickListener {
            finish()
        }
    }
}
