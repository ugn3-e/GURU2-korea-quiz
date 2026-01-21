package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// 오답 상세 화면 Activity
// 오답 노트에서 문제 클릭 시 진입
// 신조어 / 맞춤법 타입에 따라 서로 다른 레이아웃 사용
// quiz_id를 통해 원본 문제 정보를 DB에서 조회

class WrongDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            val img = findViewById<ImageView>(R.id.ivExample)
            val resId = resources.getIdentifier(quiz.exampleImage, "drawable", packageName)

            // 이미지 리소스가 존재할 때만 설정 (0이면 없음)
            if (resId != 0) img.setImageResource(resId)

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

            // 디미 유진_문장 예시 표시
            findViewById<TextView>(R.id.tvSentence).text = quiz.sentence

            // 디미 유진_오답 설명 표시
            findViewById<TextView>(R.id.tvIncorrectExp).text = quiz.incorrect_exp

            // 디미 유진_뒤로 가기 버튼
            findViewById<Button>(R.id.btnBack).setOnClickListener {
                finish()
            }
        }
    }
}
