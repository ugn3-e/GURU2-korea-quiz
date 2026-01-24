package com.example.guru2.login

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import android.content.Intent
import com.example.guru2.login.LoginActivity
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private fun toEmail(username: String): String = "${username}@guru2.local"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etId = findViewById<EditText>(R.id.etId)
        val etPw = findViewById<EditText>(R.id.etPw)
        val etNick = findViewById<EditText>(R.id.etNickname)
        val etAge = findViewById<EditText>(R.id.etAge)
        val spGender = findViewById<Spinner>(R.id.spGender)
        val spCountry = findViewById<Spinner>(R.id.spCountry)

        /* 국적 Spinner 세팅 */
        val countryList = Locale.getISOCountries()
            .map { code ->
                Locale("", code).displayCountry
            }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        val countryAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            countryList
        )

        countryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spCountry.adapter = countryAdapter

        findViewById<Button>(R.id.btnSignup).setOnClickListener {
            val username = etId.text.toString().trim()
            val password = etPw.text.toString()
            val nickname = etNick.text.toString().trim()
            val ageText = etAge.text.toString().trim()

            val gender = spGender.selectedItem.toString()
            val country = spCountry.selectedItem.toString()

            val email = toEmail(username)

            // 나이 숫자로 입력 받기
            val age = ageText.toIntOrNull()
            if (age == null) {
                Toast.makeText(this, "나이는 숫자로 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                    val profile = hashMapOf(
                        "username" to username,
                        "nickname" to nickname,
                        "gender" to gender,
                        "age" to age,
                        "country" to country,
                        "solved_count" to 0,
                        "createdAt" to System.currentTimeMillis()
                    )

                    // users/{uid} 에 프로필 저장
                    db.collection("users").document(uid).set(profile)
                        .addOnSuccessListener {
                            Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "프로필 저장 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "회원가입 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }
}
