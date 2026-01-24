package com.example.guru2.login

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.home.MainActivity
import com.example.guru2.R
import com.google.firebase.auth.FirebaseAuth
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.util.LevelUtil

class LoginActivity : AppCompatActivity() {

    // 로그인 파이어베이스
    private lateinit var auth: FirebaseAuth

    // 아이디 = 이메일
    private fun toEmail(username: String): String = "${username}@guru2.local"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // FirebaseAuth 초기화
        auth = FirebaseAuth.getInstance()

        val etId = findViewById<EditText>(R.id.etId)
        val etPw = findViewById<EditText>(R.id.etPw)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignup = findViewById<Button>(R.id.btnSignup)

        btnLogin.setOnClickListener {
            // 입력값 가져오기
            val username = etId.text.toString().trim()
            val password = etPw.text.toString()

            // 아이디/비번 공백
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "아이디/비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 아이디를 파이어베이스용 이메일로 변환
            val email = toEmail(username)

            // 파이어베이스 이메일/비번 로그인 시도
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // 로그인 성공 시 고유 uid 가져오기
                    val uid = auth.currentUser?.uid
                    if (uid == null) return@addOnSuccessListener

                    // uid 저장 (어디서든 사용자 식별 가능)
                    getSharedPreferences("auth", MODE_PRIVATE)
                        .edit()
                        .putString("uid", uid)
                        .apply()

                    // 로그인 성공 -> 메인 화면
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "로그인 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // 회원가입
        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}