package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide

// 오답 상세 화면 Activity
// 오답 노트에서 문제 클릭 시 진입
// 신조어 / 맞춤법 타입에 따라 서로 다른 레이아웃 사용
// quiz_id를 통해 원본 문제 정보를 DB에서 조회

class WrongDetailActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 툴바
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "오답" // 타이틀 설정
            // 만약 뒤로가기 버튼이 필요하면
            // setDisplayHomeAsUpEnabled(true)
        }

        // 디미 유진_이전 화면에서 전달받은 데이터
        // quiz_type: "slang" / "spelling"
        // quiz_id: 클릭한 문제의 ID
        val type = intent.getStringExtra("quiz_type") ?: "slang"
        val quizId = intent.getIntExtra("quiz_id", -1)

        // 디미 유진_신조어 오답 상세 화면
        if (type == "slang") {
            setContentView(R.layout.activity_wrong_detail_slang)

            //디미 유진_신조어 DB에서 해당 quiz_id 문제 조회
            val quiz = SlangDBManager(this).getQuizById(quizId) ?: return

            // 디미 유진_신조어 단어 표시
            findViewById<TextView>(R.id.tvSlangWord).text = quiz.slangWord

            // 디미 유진_설명(해설) 표시
            findViewById<TextView>(R.id.tvExplanation).text = quiz.explanation

            // 디미 유진_주의사항 표시
            findViewById<TextView>(R.id.tvNotice).text = quiz.notice

            // 디미 유진_예시 이미지 표시
            val imgExample = findViewById<ImageView>(R.id.ivExample)
            // 🔥 이미지 파일명 자동 보정
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

            //디미 유진_뒤로 가기 버튼
            // -> 현재 Activity만 종료 -> 오답 목록 화면
            findViewById<Button>(R.id.btnBack).setOnClickListener {
                finish()
            }

            //디미 유진_메인 화면으로 이동 코드 (시용 X)
//            val backClick = View.OnClickListener {
//                val intent = Intent(this, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                startActivity(intent)
//                finish()
//            }
//
//            findViewById<Button>(R.id.btnBack).setOnClickListener(backClick)

        // 디미 유진_맞춤법 오답 상세 화면
        } else {
            setContentView(R.layout.activity_wrong_detail_spelling)

            // 디미 유진_맞춤법 DB에서 문제 조회
            val quiz = SpellDBManager(this).getQuizById(quizId) ?: return

            // 디미 유진_정답 단어 표시
            findViewById<TextView>(R.id.tvCorrect).text = quiz.correct

            // 빈칸("____")을 정답으로 치환
            val filledSentence = quiz.sentence.replace("____", quiz.correct)

            // 문장 TextView에 표시
            findViewById<TextView>(R.id.tvSentence).text = filledSentence

            // 디미 유진_문장 예시 표시
            //findViewById<TextView>(R.id.tvSentence).text = quiz.sentence

            // 디미 유진_오답 설명 표시
            findViewById<TextView>(R.id.tvIncorrectExp).text = quiz.incorrect_exp

            // Glide로 asset에서 이미지 가져오기
            val imageView = findViewById<ImageView>(R.id.imgPlaceholder)

            if (!quiz.image_path.isNullOrEmpty()) {
                com.bumptech.glide.Glide.with(this)
                    .load("file:///android_asset/images/${quiz.image_path}")
                    .error(R.drawable.ic_launcher_foreground) // 실패 시 기본 이미지
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // 디미 유진_뒤로 가기 버튼
            findViewById<Button>(R.id.btnBack).setOnClickListener {
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mainToolbar -> { // 메뉴 ID에 맞게 수정
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
