package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.activity.OnBackPressedCallback
import android.widget.Toast
import android.widget.Toolbar
import com.bumptech.glide.Glide

class SlangResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slang_result)

        // View 연결
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val tvExplanation = findViewById<TextView>(R.id.tvExplanation)
        val tvNotice = findViewById<TextView>(R.id.tvNotice)
        val imgExample = findViewById<ImageView>(R.id.imgExample)
        val btnNext = findViewById<Button>(R.id.btnNext)

        // Intent 데이터 받기
        val isCorrect = intent.getBooleanExtra("isCorrect", false)
        val explanation = intent.getStringExtra("explanation") ?: ""
        val notice = intent.getStringExtra("notice") ?: ""
        val rawExampleImage = intent.getStringExtra("exampleImage") ?: ""

        // .png 자동 보정
        val exampleImage = if (
            rawExampleImage.isNotBlank() &&
            !rawExampleImage.contains(".")
        ) {
            "$rawExampleImage.png"
        } else {
            rawExampleImage
        }

        val nextQuizId = intent.getIntExtra("nextQuizId", -1)
        val isEndOfPart = intent.getBooleanExtra("isEndOfPart", false)

        // 상황 예시 이미지 잘 받았나 테스트용 코드
        Log.d("RESULT", "rawExampleImage = [$rawExampleImage]")
        Log.d("RESULT", "fixedExampleImage = [$exampleImage]")

        // 정답 / 오답 표시
        if (isCorrect) {
            tvResult.text = "정답!"
            tvResult.setTextColor(
                ContextCompat.getColor(this, android.R.color.holo_green_dark)
            )
        } else {
            tvResult.text = "오답!"
            tvResult.setTextColor(
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
            )
        }

        // 해설 / 주의사항
        tvExplanation.text = explanation
        tvNotice.text = notice

        // 주의사항이 없으면 숨김
        if (notice.isBlank()) {
            tvNotice.visibility = TextView.GONE
        }

        // 상황 이미지 가져오기
        if (exampleImage.isNotBlank()) {
            Glide.with(this)
                .load("file:///android_asset/slang_image/$exampleImage")
                .into(imgExample)

            imgExample.visibility = View.VISIBLE
        } else {
            imgExample.visibility = View.GONE
        }

        btnNext.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("isEndOfPart", isEndOfPart)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // 뒤로가기 막기
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(
                        this@SlangResultActivity,
                        "다음 버튼을 눌러 진행해주세요!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }
}