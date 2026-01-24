package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity


class SavedDetail : AppCompatActivity() {
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_detail)

        // 툴바
        val mainToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        setSupportActionBar(mainToolbar)

        val tvTitle = findViewById<TextView>(R.id.saved_title)
        val ivImg = findViewById<ImageView>(R.id.saved_img)
        val tvSentence = findViewById<TextView>(R.id.saved_sentence)
        val tvExp = findViewById<TextView>(R.id.saved_exp)
        val btnHome = findViewById<Button>(R.id.button2)

        // Intent 데이터 받기
        val quizId = intent.getIntExtra("quiz_id", -1)

        if(quizId != -1) {
            // 전달받은 ID로 DB에서 문제 객체 가져옴
            val spellDbManager = SpellDBManager(this)
            val quiz = spellDbManager.getQuizById(quizId)

            // DB 가져오기
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

        // 뒤로 가기
        btnHome.setOnClickListener {
            finish()
        }
    }

    // 메뉴 연결
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                val intent = Intent(this, SurveyActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_mypage -> {
                startActivity(Intent(this, com.example.guru2.mypage.MyPageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}