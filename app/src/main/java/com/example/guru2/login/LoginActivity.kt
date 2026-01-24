package com.example.guru2.login

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.home.MainActivity
import com.example.guru2.R
import com.google.firebase.auth.FirebaseAuth

// 로그인 화면
class LoginActivity : AppCompatActivity() {

    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    // 아이디 → Firebase 이메일
    private fun toEmail(username: String): String = "${username}@guru2.local"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // FirebaseAuth 초기화
        auth = FirebaseAuth.getInstance()

        // 입력 UI 연결
        val etId = findViewById<EditText>(R.id.etId)
        val etPw = findViewById<EditText>(R.id.etPw)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignup = findViewById<Button>(R.id.btnSignup)

        // 로그인 버튼 클릭
        btnLogin.setOnClickListener {
            // 입력값 가져오기
            val username = etId.text.toString().trim()
            val password = etPw.text.toString()

            // 아이디/비번 공백
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "아이디/비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // username → email 변환
            val email = toEmail(username)

            // Firebase 이메일/비번 로그인 시도
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

                    // 로그인 성공 → 메인 화면
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                // 로그인 실패
                .addOnFailureListener {
                    Toast.makeText(this, "로그인 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // 회원가입 화면 이동
        btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}