package com.example.guru2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.FragmentContainer

class ResultActivity1 : AppCompatActivity() {
    lateinit var DBManager: DBManager
    lateinit var resultText: TextView
    lateinit var quizText: TextView
    lateinit var quizAnswer: TextView
    lateinit var bubbleOverlay: FrameLayout
    lateinit var infoText: TextView
    lateinit var btnInfo: TextView
    lateinit var imgDog: ImageView
    lateinit var bubbleContainer: FrameLayout

    lateinit var btnNext: Button
    lateinit var btnSave: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result1)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ui 연결
        resultText = findViewById<TextView>(R.id.ResultText)
        quizText = findViewById<TextView>(R.id.QuizText)
        quizAnswer = findViewById<TextView>(R.id.QuizAnswer)
        btnNext = findViewById<Button>(R.id.btnNext)
        btnSave = findViewById<Button>(R.id.btnSave)

        // 콘텐츠 정보 보기 연결
        bubbleOverlay = findViewById<FrameLayout>(R.id.bubbleOverlay)
        bubbleContainer = findViewById<FrameLayout>(R.id.bubbleContainer)
        infoText = findViewById<TextView>(R.id.infoText)
        btnInfo = findViewById<TextView>(R.id.btnInfo)
        imgDog = findViewById<ImageView>(R.id.imgDog)


        // DBManager
        val dbManager = DBManager(this)

        // 데이터 가져오기
        // quizId, isCorrect
        // 문제 순서, 정답/오답 여부 판단
        val quizId = intent.getIntExtra("quiz_id", -1)
        val isCorrect = intent.getBooleanExtra("is_correct", false)
        val quiz = dbManager.getQuizById(quizId)
        val quizCount = intent.getIntExtra("quiz_count", 1)

        // 콘텐츠 정보 불러오기
        val correctExp = intent.getStringExtra("correct_exp") ?: ""

        // 누적값 다시 전달 // ☑️ 추가
        val totalSCount = intent.getIntExtra("totalSCount", 0)
        val correctSCount = intent.getIntExtra("correctSCount", 0)

        // 더미데이터 넣기
        //isCorrect if문
        quiz?.let {
            quizText.text = it.sentence.replace("____", it.correct)
            if(isCorrect) {
                resultText.text = "정답!"
                quizAnswer.text = it.incorrect_exp
            } else {
                resultText.text = "오답"
                quizAnswer.text = it.incorrect_exp
            }


        }

        // 콘텐츠 정보 보기
        btnInfo.setOnClickListener {
            // 보였다 ↔ 숨겼다
            btnInfo.visibility = View.GONE
            bubbleOverlay.visibility = View.VISIBLE

            // 말풍선 안 텍스트 설정
            infoText.text = correctExp

            bubbleOverlay.visibility = View.VISIBLE

            // 강아지 위치 얻기 (화면 기준)
            // 말풍선을 화면 위에 띄움(Overlay 유지) + 모든 기기에서 똑같은 위치
            val location = IntArray(2)
            imgDog.getLocationOnScreen(location)

            // 말풍선을 강아지 위쪽에 배치
            bubbleContainer.post {
                val dogX = imgDog.x
                val dogY = imgDog.y

                bubbleContainer.x =
                    dogX + imgDog.width.toFloat() * 0.6f

                bubbleContainer.y =
                    dogY - bubbleContainer.height.toFloat() + 120f
            }
        }

        // 말풍선 눌렀을 때 숨겨짐
        infoText.setOnClickListener {
            // 보였다 ↔ 숨겼다
            bubbleOverlay.visibility = View.GONE
            btnInfo.visibility = View.VISIBLE
        }


        // 다음 문제로 이동
        btnNext.setOnClickListener {
            val intent = Intent(this, QuizActivity1::class.java)

            // 다음 문제로 이동
            // startActivity(Intent(this, QuizActivity1::class.java))
            intent.putExtra("quiz_count", quizCount + 1)

            // 누적값 다시 전달 // ☑️ 추가
            intent.putExtra("totalSCount", totalSCount)
            intent.putExtra("correctSCount", correctSCount)

            startActivity(intent)
            finish() // 뒤로 가기 누를 시 퀴즈 화면

        }

        // 콘텐츠 저장
        btnSave.setOnClickListener {
            // 콘텐츠 저장
        }
    }
}