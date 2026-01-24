package com.example.guru2.mypage

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        /* AppBar*/
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        /* View */
        val etNickname = findViewById<EditText>(R.id.etNickname)
        val etAge = findViewById<EditText>(R.id.etAge)
        val spGender = findViewById<Spinner>(R.id.spGender)
        val spCountry = findViewById<Spinner>(R.id.spCountry)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        val uid = auth.currentUser?.uid ?: run {
            finish()
            return
        }

        /* 국가 Spinner 세팅 */
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

        /*  Firestore → 기존 정보 prefill  */
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener

                val nickname = doc.getString("nickname") ?: ""
                val age = doc.getLong("age")?.toString() ?: ""
                val gender = doc.getString("gender") ?: ""
                val country = doc.getString("country") ?: ""

                // EditText prefill
                etNickname.setText(nickname)
                etAge.setText(age)

                // Spinner 국가 prefill
                val genderAdapter = spGender.adapter as ArrayAdapter<String>
                val genderPos = genderAdapter.getPosition(gender)
                if (genderPos >= 0) spGender.setSelection(genderPos)

                val countryPos = countryAdapter.getPosition(country)
                if (countryPos >= 0) spCountry.setSelection(countryPos)
            }

        /* 저장 */
        btnSave.setOnClickListener {
            val nickname = etNickname.text.toString().trim()
            val ageText = etAge.text.toString().trim()
            val gender = spGender.selectedItem.toString()
            val country = spCountry.selectedItem.toString()

            val age = ageText.toIntOrNull()
            if (nickname.isBlank() || age == null) {
                Toast.makeText(this, "닉네임과 나이를 확인하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updates = mapOf(
                "nickname" to nickname,
                "age" to age,
                "gender" to gender,
                "country" to country
            )

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

        btnCancel.setOnClickListener {
            finish()
        }
    }
}
