package com.example.guru2.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.MainActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 🔹 Auth Repository (SQLite 기반)
        val repo = AuthRepository(SQLiteAuthDataSource(this))

        // 🔹 View 연결
        val etId = findViewById<EditText>(R.id.etId)
        val etPw = findViewById<EditText>(R.id.etPw)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignup = findViewById<Button>(R.id.btnSignup)

        // 🔹 로그인 버튼
        btnLogin.setOnClickListener {

            // 입력값 체크
            if (etId.text.isBlank() || etPw.text.isBlank()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = repo.login(
                etId.text.toString(),
                etPw.text.toString()
            )

            if (user != null) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                // TODO: 메인 화면으로 이동 (있다면)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "아이디 또는 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔹 회원가입 버튼 → 회원가입 화면 이동
        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
