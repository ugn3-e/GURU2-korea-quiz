package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SavedDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_saved_detail)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val tvTitle = findViewById<TextView>(R.id.saved_title)
        val ivImg = findViewById<ImageView>(R.id.saved_img)
        val tvSentence = findViewById<TextView>(R.id.saved_sentence)
        val tvExp = findViewById<TextView>(R.id.saved_exp)
        val btnHome = findViewById<Button>(R.id.button2)

        // Intent 데이터 받기
        val quizId = intent.getIntExtra("quiz_id", -1)

        if(quizId != -1) {
            val spellDbManager = SpellDBManager(this)
            val quiz = spellDbManager.getQuizById(quizId)

            quiz?.let {
                android.util.Log.d("IMG_CHECK", "DB에서 가져온 파일명: ${it.image_path}")
                tvTitle.text = it.source ?: "출처 정보 없음"

                tvSentence.text = it.sentence.replace("____", it.correct)

                tvExp.text = it.correct_exp

                if (!it.image_path.isNullOrEmpty()) {
                    // Glide를 사용하여 assets/images/ 폴더 내의 파일 로드
                    com.bumptech.glide.Glide.with(this)
                        .load("file:///android_asset/images/${it.image_path}")
                        .into(ivImg)
                } else {
                    // 이미지가 없을 경우 기본 이미지 설정
                    ivImg.setImageResource(R.drawable.ic_launcher_foreground)
                }
            }
        }

        // (수정) 뒤로 가기
        btnHome.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //?
//            startActivity(intent)
            finish()
        }
    }
}