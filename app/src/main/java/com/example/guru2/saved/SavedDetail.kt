package com.example.guru2.saved

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.guru2.R
import com.example.guru2.mypage.MyPageActivity
import com.example.guru2.spellquiz.SpellDBManager
import com.example.guru2.survey.SurveyActivity

// 저장한 콘텐츠 상세 화면
// 문장, 정답, 정보, 이미지 표시
class SavedDetail : AppCompatActivity() {
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_detail)

        // 툴바
        val mainToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        setSupportActionBar(mainToolbar)

        // View 연결
        val tvTitle = findViewById<TextView>(R.id.saved_title)
        val ivImg = findViewById<ImageView>(R.id.saved_img)
        val tvSentence = findViewById<TextView>(R.id.saved_sentence)
        val tvExp = findViewById<TextView>(R.id.saved_exp)
        val btnHome = findViewById<Button>(R.id.button2)

        // Intent 데이터 받기(quizId)
        val quizId = intent.getIntExtra("quiz_id", -1)

        if(quizId != -1) {
            // 전달받은 ID로 DB에서 문제 객체 가져옴
            val spellDbManager = SpellDBManager(this)
            val quiz = spellDbManager.getQuizById(quizId)

            // DB 가져오기
            quiz?.let {
                // 출처
                Log.d("IMG_CHECK", "DB에서 가져온 파일명: ${it.image_path}")
                tvTitle.text = it.source ?: "출처 정보 없음"

                // 빈칸 문장 → 정답
                tvSentence.text = it.sentence.replace("____", it.correct)

                // 해설
                tvExp.text = it.correct_exp

                // 이미지 로드
                if (!it.image_path.isNullOrEmpty()) {
                    // Glide를 사용하여 assets/images/ 폴더 내의 파일 로드
                    Glide.with(this)
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

    // 상단 메뉴 연결
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 상단 메뉴 클릭
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                val intent = Intent(this, SurveyActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_mypage -> {
                startActivity(Intent(this, MyPageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}