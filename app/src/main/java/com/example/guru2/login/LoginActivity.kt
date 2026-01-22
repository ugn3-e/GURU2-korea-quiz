package com.example.guru2.login

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.MainActivity
import com.example.guru2.R
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.util.LevelUtil

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val repo = AuthRepository(SQLiteAuthDataSource(this))

        val etId = findViewById<EditText>(R.id.etId)
        val etPw = findViewById<EditText>(R.id.etPw)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignup = findViewById<Button>(R.id.btnSignup)

        btnLogin.setOnClickListener {
            val user = repo.login(
                etId.text.toString(),
                etPw.text.toString()
            )

            if (user != null) {

                // 디미 유진_로그인 성공 -> userId 저장
                getSharedPreferences("auth", MODE_PRIVATE)
                    .edit()
                    .putLong("user_id", user.id)
                    .apply()

                // 메인으로 이동
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}