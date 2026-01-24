package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.guru2.fire.FirestoreSavedContent

class SpellResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spell_result)

        // ================= View =================
        val tvResult = findViewById<TextView>(R.id.ResultText)
        val tvQuizText = findViewById<TextView>(R.id.QuizText)
        val tvAnswer = findViewById<TextView>(R.id.QuizAnswer)
        val imgResult = findViewById<ImageView>(R.id.resultImg)

        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnInfo = findViewById<TextView>(R.id.btnInfo)

        val bubbleOverlay = findViewById<FrameLayout>(R.id.bubbleOverlay)
        val bubbleContainer = findViewById<FrameLayout>(R.id.bubbleContainer)
        val infoText = findViewById<TextView>(R.id.infoText)
        val imgDog = findViewById<ImageView>(R.id.imgDog)

        // ================= Intent 데이터 =================
        val isCorrect = intent.getBooleanExtra("isCorrect", false)
        val sentence = intent.getStringExtra("sentence") ?: ""
        val correctAnswer = intent.getStringExtra("correctAnswer") ?: ""
        val correctExp = intent.getStringExtra("correct_exp") ?: ""
        val incorrectExp = intent.getStringExtra("incorrect_exp") ?: ""
        val imagePath = intent.getStringExtra("image_path") ?: ""
        val quizId = intent.getIntExtra("quiz_id", -1)
        val isEndOfPart = intent.getBooleanExtra("isEndOfPart", false)

        // ================= 결과 표시 =================
        tvResult.text = if (isCorrect) "정답!" else "오답"
        tvResult.setTextColor(
            ContextCompat.getColor(
                this,
                if (isCorrect) android.R.color.holo_green_dark
                else android.R.color.holo_red_dark
            )
        )

        val filledSentence =
            if (sentence.contains("____")) {
                sentence.replace("____", correctAnswer)
            } else {
                sentence
            }

        tvQuizText.text = filledSentence

        tvAnswer.text = incorrectExp

        // ================= 이미지 표시 (🔥 누락 해결) =================
        if (imagePath.isNotBlank()) {
            Glide.with(this)
                .load("file:///android_asset/images/$imagePath")
                .into(imgResult)
            imgResult.visibility = View.VISIBLE
        } else {
            imgResult.visibility = View.GONE
        }

        // ================= 콘텐츠 정보 말풍선 =================
        btnInfo.setOnClickListener {
            btnInfo.visibility = View.GONE
            bubbleOverlay.visibility = View.VISIBLE

            infoText.text = correctExp
            infoText.setTextColor(getColor(R.color.confirm_active))
            infoText.setTypeface(null, android.graphics.Typeface.BOLD)

            bubbleContainer.post {
                bubbleContainer.x = imgDog.x + imgDog.width * 0.6f
                bubbleContainer.y = imgDog.y - bubbleContainer.height + 120f
            }
        }

        infoText.setOnClickListener {
            bubbleOverlay.visibility = View.GONE
            btnInfo.visibility = View.VISIBLE
        }

        // ================= 다음 문제 (🔥 slang 구조 동일) =================
        btnNext.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("isEndOfPart", isEndOfPart)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // ================= 콘텐츠 저장 (🔥 quiz_id 연결 복구) =================
        btnSave.setOnClickListener {
            if (quizId == -1) {
                Toast.makeText(this, "저장할 수 없는 문제입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sdf = java.text.SimpleDateFormat("dd\nMMM", java.util.Locale.ENGLISH)
            val date = sdf.format(java.util.Date())

            FirestoreSavedContent().saveSpell(
                quizId = quizId,
                savedDate = date,
                onSuccess = {
                    Toast.makeText(this, "보관함에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    btnSave.isEnabled = false
                    btnSave.text = "저장됨"
                },
                onFail = { e ->
                    Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // ================= 뒤로가기 차단 =================
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(
                        this@SpellResultActivity,
                        "다음 버튼을 눌러 진행해주세요!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

//    // ================= 메뉴 =================
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_survey -> {
//                startActivity(Intent(this, SurveyActivity::class.java))
//                true
//            }
//            R.id.action_mypage -> {
//                startActivity(
//                    Intent(this, com.example.guru2.mypage.MyPageActivity::class.java)
//                )
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}
