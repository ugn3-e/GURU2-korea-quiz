package com.example.guru2

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// 디미 유진_오답 상세화면
class WrongDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrong_detail)

        // 디미 유진_전달 받은 문제
        val quizId = intent.getIntExtra("quiz_id", -1)

        // 디미 유진_문제DB에서 문제 조회
        val quiz = SlangDBManager(this).getQuizById(quizId)

        // 디미 유진_문제 정보 화면 조회(문제 문장, 설명, 주의 사항)
        findViewById<TextView>(R.id.tvQuestion).text = quiz?.question
        findViewById<TextView>(R.id.tvExplanation).text = quiz?.explanation
        findViewById<TextView>(R.id.tvNotice).text = quiz?.notice

        // 디미 유진_상황 예시 이미지
        quiz?.exampleImage?.let {
            val resId = resources.getIdentifier(it, "drawable", packageName)
            findViewById<ImageView>(R.id.imgExample).setImageResource(resId)
        }
    }
}
