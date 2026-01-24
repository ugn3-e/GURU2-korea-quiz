package com.example.guru2.wrong

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.guru2.R
import com.example.guru2.slangquiz.SlangDBManager
import com.example.guru2.spellquiz.SpellDBManager
import com.example.guru2.mypage.MyPageActivity
import com.example.guru2.survey.SurveyActivity

// 오답 상세 화면 Activity
// 오답 노트에서 문제 클릭 시 진입
// 신조어 / 맞춤법 타입에 따라 서로 다른 레이아웃 사용
// quiz_id를 통해 문제 정보를 DB에서 조회

class WrongDetailActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 이전 화면에서 전달받은 데이터
        // quiz_type: "slang" / "spelling"
        // quiz_id: 클릭한 문제의 ID
        val type = intent.getStringExtra("quiz_type") ?: "slang"
        val quizId = intent.getIntExtra("quiz_id", -1)

        // 신조어 오답 상세 화면
        if (type == "slang") {
            setContentView(R.layout.activity_wrong_detail_slang)

            // 신조어 DB에서 해당 quiz_id 문제 조회
            val quiz = SlangDBManager(this).getQuizById(quizId) ?: return

            // 신조어 단어 표시
            findViewById<TextView>(R.id.tvSlangWord).text = quiz.slangWord

            // 해설 표시
            findViewById<TextView>(R.id.tvExplanation).text = quiz.explanation

            // 주의사항 표시
            findViewById<TextView>(R.id.tvNotice).text = quiz.notice

            // 이미지 표시
            val imgExample = findViewById<ImageView>(R.id.ivExample)
            val rawImage = quiz.exampleImage ?: ""
            val imageFile = if (rawImage.isNotBlank() && !rawImage.contains(".")) {
                "$rawImage.png"
            } else {
                rawImage
            }

            if (imageFile.isNotBlank()) {
                Glide.with(this)
                    .load("file:///android_asset/slang_image/$imageFile")
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imgExample)

                imgExample.visibility = View.VISIBLE
            } else {
                imgExample.visibility = View.GONE
            }

            // 뒤로 가기 버튼 -> 현재 Activity만 종료 -> 오답 목록 화면
            findViewById<Button>(R.id.btnBack).setOnClickListener {
                finish()
            }

        // 맞춤법 오답 상세 화면
        } else {
            setContentView(R.layout.activity_wrong_detail_spelling)

            // 맞춤법 DB에서 문제 조회
            val quiz = SpellDBManager(this).getQuizById(quizId) ?: return

            // 정답 단어 표시
            findViewById<TextView>(R.id.tvCorrect).text = quiz.correct

            // 빈칸 부분에 정답 넣기
            val filledSentence = quiz.sentence.replace("____", quiz.correct)

            // 문장 표시
            findViewById<TextView>(R.id.tvSentence).text = filledSentence

            // 오답 해설 표시
            findViewById<TextView>(R.id.tvIncorrectExp).text = quiz.incorrect_exp

            // Glide로 asset에서 이미지 가져오기
            val imageView = findViewById<ImageView>(R.id.imgPlaceholder)

            if (!quiz.image_path.isNullOrEmpty()) {
                Glide.with(this)
                    .load("file:///android_asset/images/${quiz.image_path}")
                    .error(R.drawable.ic_launcher_foreground) // 실패 시 기본 이미지
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // 뒤로 가기 버튼
            findViewById<Button>(R.id.btnBack).setOnClickListener {
                finish()
            }
        }

        // 툴바
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "오답" // 타이틀 설정
        }
    }

    // 메뉴 연결
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 설문조사로 이동
            R.id.action_survey -> {
                val intent = Intent(this, SurveyActivity::class.java)
                startActivity(intent)
                return true
            }

            // 마이페이지 이동
            R.id.action_mypage -> {
                startActivity(Intent(this, MyPageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}