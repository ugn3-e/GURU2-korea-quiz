package com.example.guru2.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.login.LoginActivity
import com.example.guru2.util.LevelUtil

class MyPageActivity : AppCompatActivity() {

    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page2)

        val tvNickname = findViewById<TextView>(R.id.tvNickname)
        val tvInfo = findViewById<TextView>(R.id.tvInfo)
        val tvLevel = findViewById<TextView>(R.id.tvLevel)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val pref = getSharedPreferences("auth", MODE_PRIVATE)
        userId = pref.getLong("user_id", -1L)

        if (userId != -1L) {
            val db = SQLiteAuthDataSource(this).readableDatabase
            val cursor = db.rawQuery(
                "SELECT nickname, gender, age, country, solved_count FROM users WHERE id = ?",
                arrayOf(userId.toString())
            )

            if (cursor.moveToFirst()) {
                val nickname = cursor.getString(0)
                val gender = cursor.getString(1)
                val age = cursor.getInt(2)
                val country = cursor.getString(3)
                val solvedCount = cursor.getInt(4)

                val level = LevelUtil.calculateLevel(solvedCount)

                tvNickname.text = nickname
                tvInfo.text = "$gender · ${age}세 · $country"
                tvLevel.text = "Lv.$level · 푼 문제 $solvedCount"
            }
            cursor.close()
        }

        btnLogout.setOnClickListener {
            pref.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
