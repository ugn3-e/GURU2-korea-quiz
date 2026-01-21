package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback
import android.widget.Toast

class SlangQuizActivity : AppCompatActivity() {

    // 디미 유진_결과 화면 -> 다음 문제 화면으로 넘어갈 때 확인을 위한 번호
    companion object {
        private const val REQ_RESULT = 1001
    }

    // 디미 유진_전체 결과 누적 변수
    private var totalCount = 0
    private var correctCount = 0

    // 디미 유진_Q 번호
    private var currentQuizId = 1
    private lateinit var tvQNumber: TextView

    // 디미 유진_문제
    private lateinit var tvQuestion: TextView

    // 디미 유진_선택지 버튼
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var choiceButtons: List<Button>

    // 디미 유진_상황 예시 이미지
    private lateinit var btnShowExample: TextView
    private lateinit var imgExample: ImageView

    // 디미 유진_확인 버튼
    private lateinit var btnConfirm: Button

    // 디미 유진_선택지 값
    private var selectedAnswer = ""
    private lateinit var currentQuiz: SlangQuizData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slang_quiz)

        // 디미 유진_View 연결
        tvQNumber = findViewById(R.id.QText)
        tvQuestion = findViewById(R.id.tvQuestion)

        btn1 = findViewById(R.id.btnChoice1)
        btn2 = findViewById(R.id.btnChoice2)
        btn3 = findViewById(R.id.btnChoice3)
        btn4 = findViewById(R.id.btnChoice4)

        btnShowExample = findViewById(R.id.btnShowExample)
        imgExample = findViewById(R.id.imgExample)
        btnConfirm = findViewById(R.id.btnConfirm)

        choiceButtons = listOf(btn1, btn2, btn3, btn4)

        // 디미 유진_초기 UI 상태 (확인버튼, 상황 예시 이미지 X)
        resetChoiceButtons()
        btnConfirm.isEnabled = false
        btnConfirm.setBackgroundColor(getColor(R.color.confirm_default))
        imgExample.visibility = View.GONE

        // 디미 유진_선택지 클릭 이벤트
        btn1.setOnClickListener { onChoiceSelected(btn1) }
        btn2.setOnClickListener { onChoiceSelected(btn2) }
        btn3.setOnClickListener { onChoiceSelected(btn3) }
        btn4.setOnClickListener { onChoiceSelected(btn4) }

        // 디미 유진_상황 예시 이미지 보기
        btnShowExample.setOnClickListener {
            imgExample.visibility =
                if (imgExample.visibility == View.VISIBLE)
                    View.GONE
                else
                    View.VISIBLE
        }

        // 디미 유진_정답 확인 -> 결과 화면 이동
        btnConfirm.setOnClickListener {
            moveToResultPage()
        }

        // 이어서 학습하기 선택 ☑️
        val startId = intent.getIntExtra("startQuizId", -1)
        if (startId != -1) {
            currentQuizId = startId
        }

        // 디미 유진_DB에서 문제 불러오기
        loadQuizFromDB()

        // 디미 유진_뒤로가기 막기
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(
                        this@SlangQuizActivity,
                        "퀴즈를 완료해야 홈으로 돌아갈 수 있어요!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    // 디미 유진_DB에서 문제 1개 가져오기
    private fun loadQuizFromDB() {
        val dbManager = SlangDBManager(this)
        val quiz = dbManager.getQuizById(currentQuizId)

        // Q1~5만 반복 ☑️
        val qNumber = ((currentQuizId - 1) % 5) + 1

        // 디미 유진_문제 다 풀었을 경우
        // >> (수정) 마지막 문제 안내 페이지로 연결
        if (quiz == null) {
            //Toast.makeText(this, "모든 문제를 풀었습니다 🎉", Toast.LENGTH_SHORT).show()
            moveToFinalResultPage()
            return
        }

        currentQuiz = quiz

        // 디미 유진_불러온 Q 번호
        tvQNumber.text = "Q$qNumber"

        // 디미 유진_불러온 문제 & 선택지
        tvQuestion.text = quiz.question

        // 디미 유진_원본 선택지 로그
        Log.d("Clog", "Q${quiz.id} 원본 보기 = ${listOf(
            quiz.choice1, quiz.choice2, quiz.choice3, quiz.choice4
        )}")

        // 디미 유진_선택지 섞기
        setShuffledChoices(quiz)


        // 디미 유진_불러온 문제의 상황 예시 이미지
        val imageResId = resources.getIdentifier(
            quiz.exampleImage,
            "drawable",
            packageName
        )

        imgExample.visibility = View.GONE
        if (imageResId != 0) {
            imgExample.setImageResource(imageResId)
        }

        // 디미 유진_선택지, 확인 버튼 초기화
        resetChoiceButtons()
        btnConfirm.isEnabled = false
        btnConfirm.setBackgroundColor(getColor(R.color.confirm_default))
    }


    // 디미 유진_선택지 클릭 처리
    private fun onChoiceSelected(selectedButton: Button) {
        resetChoiceButtons()

        selectedButton.setBackgroundColor(getColor(R.color.choice_selected))
        selectedAnswer = selectedButton.text.toString()

        btnConfirm.isEnabled = true
        btnConfirm.setBackgroundColor(getColor(R.color.confirm_active))
    }

    // 디미 유진_선택지 버튼 초기화
    private fun resetChoiceButtons() {
        for (btn in choiceButtons) {
            btn.setBackgroundColor(getColor(R.color.choice_default))
        }
        selectedAnswer = ""
    }

    // 디미 유진_선택지 섞기
    private fun setShuffledChoices(quiz: SlangQuizData) {
        val choices = mutableListOf(
            quiz.choice1,
            quiz.choice2,
            quiz.choice3,
            quiz.choice4
        )

        choices.shuffle()

        // 디미 유진_섞인 결과 로그
        Log.d("Clog", "Q${quiz.id} 섞인 보기 = $choices")

        val answerIndex = choices.indexOf(quiz.answer)
        Log.d("Clog", "Q${quiz.id} 정답 위치 = ${answerIndex + 1}번")

        btn1.text = choices[0]
        btn2.text = choices[1]
        btn3.text = choices[2]
        btn4.text = choices[3]
    }

    // 디미 유진_결과 화면 이동
    private fun moveToResultPage() {
        val isCorrect = selectedAnswer == currentQuiz.answer

        // 디미 유진_결과 누적
        totalCount++
        if (isCorrect) correctCount++

        // 디미 유진_오답일 경우 현재 퀴즈 질문과 선택한 답 db에 저장하기
        if (!isCorrect) {
            WrongDBManager(this)
                .saveSlangWrong(this, currentQuiz.id, selectedAnswer)
        }

        // 마지막으로 푼 문제 ID 저장 ☑️
        val pref = getSharedPreferences("slang_quiz", MODE_PRIVATE)
        pref.edit()
            .putInt("lastQuizId", currentQuizId)
            .apply()

        val intent = Intent(this, SlangResultActivity::class.java).apply {
            putExtra("isCorrect", isCorrect)
            putExtra("explanation", currentQuiz.explanation)
            putExtra("notice", currentQuiz.notice)
            putExtra("exampleImage", currentQuiz.exampleImage)
            putExtra("nextQuizId", currentQuizId + 1)
            // 5문제마다 최종 결과 화면으로 이동 ☑️
            putExtra("isEndOfPart", totalCount % 5 == 0)
        }

        startActivityForResult(intent, REQ_RESULT)
    }

    // 디미 유진_결과 화면 -> 다음 문제 화면을 위해 돌아왔을 경우
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_RESULT && resultCode == RESULT_OK) {
            // finalresult 이동 판단 ☑️
            val isEndOfPart = data?.getBooleanExtra("isEndOfPart", false) ?: false

            if (isEndOfPart) {
                moveToFinalResultPage()
                return
            }

            currentQuizId =
                data?.getIntExtra("nextQuizId", currentQuizId + 1)
                    ?: (currentQuizId + 1)

            loadQuizFromDB()
        }
    }

    // 디미 유진_최종 결과 화면 이동
    private fun moveToFinalResultPage() {
        val intent = Intent(this, SlangFinalResultActivity::class.java).apply {
            putExtra("totalCount", totalCount)
            putExtra("correctCount", correctCount)
        }
        // 맞힌 문제 초기화 ☑️
        correctCount = 0

        startActivity(intent)
    }
}