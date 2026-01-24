package com.example.guru2.mypage

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

// 프로필 수정 화면
class EditProfileActivity : AppCompatActivity() {

    // 현재 로그인 유저 uid 확인
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // 툴바(뒤로가기 버튼 활성화)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // View 연결
        val etNickname = findViewById<EditText>(R.id.etNickname)
        val etAge = findViewById<EditText>(R.id.etAge)
        val spGender = findViewById<Spinner>(R.id.spGender)
        val spCountry = findViewById<Spinner>(R.id.spCountry)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        // 로그인 유저 확인
        val uid = auth.currentUser?.uid ?: run {
            finish()
            return
        }

        // 국가 Spinner(중복 제거 + 정렬)
        val countryList = Locale.getISOCountries()
            .map { Locale("", it).displayCountry }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        val countryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            countryList
        )
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCountry.adapter = countryAdapter

        // Firestore → 기존 프로필 정보
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener

                val nickname = doc.getString("nickname") ?: ""
                val age = doc.getLong("age")?.toString() ?: ""
                val gender = doc.getString("gender") ?: ""
                val country = doc.getString("country") ?: ""

                // EditText 미리 채우기
                etNickname.setText(nickname)
                etAge.setText(age)

                // Spinner 국가 미리 채우기(성별)
                val genderAdapter = spGender.adapter as ArrayAdapter<String>
                val genderPos = genderAdapter.getPosition(gender)
                if (genderPos >= 0) spGender.setSelection(genderPos)

                // Spinner 미리 채우기(국가)
                val countryPos = countryAdapter.getPosition(country)
                if (countryPos >= 0) spCountry.setSelection(countryPos)
            }

        // 저장 버튼 클릭
        btnSave.setOnClickListener {
            val nickname = etNickname.text.toString().trim()
            val ageText = etAge.text.toString().trim()
            val gender = spGender.selectedItem.toString()
            val country = spCountry.selectedItem.toString()

            // 입력 검증
            val age = ageText.toIntOrNull()
            if (nickname.isBlank() || age == null) {
                Toast.makeText(this, "닉네임과 나이를 확인하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 업데이트할 내용들
            val updates = mapOf(
                "nickname" to nickname,
                "age" to age,
                "gender" to gender,
                "country" to country
            )

            // user/{uid} 문서 업데이트
            firestore.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "프로필이 수정되었습니다", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "수정 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // 취소 버튼
        btnCancel.setOnClickListener {
            finish()
        }
    }
}
