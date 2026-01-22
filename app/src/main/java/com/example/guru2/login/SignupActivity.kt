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

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val repo = AuthRepository(SQLiteAuthDataSource(this))

        val etId = findViewById<EditText>(R.id.etId)
        val etPw = findViewById<EditText>(R.id.etPw)
        val etNick = findViewById<EditText>(R.id.etNickname)
        val etAge = findViewById<EditText>(R.id.etAge)

        val spGender = findViewById<Spinner>(R.id.spGender)

        val spCountry = findViewById<Spinner>(R.id.spCountry)

        /* 국적 Spinner 동적 세팅 */
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

            val success = repo.signup(
                username = etId.text.toString(),
                password = etPw.text.toString(),
                nickname = etNick.text.toString(),
                gender = spGender.selectedItem.toString(),
                age = etAge.text.toString().toInt(),
                country = spCountry.selectedItem.toString()
            )

            if (success) {
                Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()

                // 로그인 화면으로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

                finish() // 회원가입 화면 종료
            } else {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
