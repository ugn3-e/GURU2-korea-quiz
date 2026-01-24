package com.example.guru2.spellquiz

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.guru2.R
import com.example.guru2.spellquiz.SpellFinalResultActivity
import com.example.guru2.spellquiz.SpellResultActivity
import com.example.guru2.fire.FirestoreLevelStore
import com.example.guru2.fire.FirestoreProgress
import com.example.guru2.fire.FirestoreWrongNote
import com.google.android.material.button.MaterialButton

class SpellQuizActivity : AppCompatActivity() {

    companion object {
        private const val REQ_RESULT = 1001
        private const val SET_SIZE = 5
    }

    // View
    private lateinit var tvQNumber: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var imgExample: ImageView
    private lateinit var imgDog: ImageView

    private lateinit var btn1: MaterialButton
    private lateinit var btn2: MaterialButton
    private lateinit var btn3: MaterialButton
    private lateinit var btnConfirm: MaterialButton
    private lateinit var choiceButtons: List<MaterialButton>

    // 상태
    private var currentQuizId = 1        // 문제 ID

    private var selectedAnswer = ""

    private var correctAnswer = ""
    private var correctExp = ""
    private var incorrectExp = ""
    private var imagePath = ""

    private var totalCount = 0
    private var correctCount = 0

    // 파이어베이스
    private val progressStore by lazy { FirestoreProgress() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spell_quiz)

        bindViews()
        setClickListeners()

        // 파이어베이스에서 다음 문제 ID만 복원
        progressStore.loadSpellNextQuizId(
            onResult = { nextId ->
                currentQuizId = nextId
                loadQuiz()
            },
            onError = {
                currentQuizId = 1
                loadQuiz()
            }
        )

        // 퀴즈 도중 뒤로가기 불가
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(
                        this@SpellQuizActivity,
                        "퀴즈를 완료해야 홈으로 돌아갈 수 있어요!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    // View
    private fun bindViews() {
        tvQNumber = findViewById(R.id.QuizNum)
        tvQuestion = findViewById(R.id.QuizText)
        imgExample = findViewById(R.id.imageView)
        imgDog = findViewById(R.id.imgDog)

        btn1 = findViewById(R.id.btnChoice1)
        btn2 = findViewById(R.id.btnChoice2)
        btn3 = findViewById(R.id.btnChoice3)
        btnConfirm = findViewById(R.id.btnSub)

        choiceButtons = listOf(btn1, btn2, btn3)

        resetChoiceButtons()
        disableConfirmButton()
    }

    // 클릭
    private fun setClickListeners() {
        btn1.setOnClickListener { onChoiceSelected(btn1) }
        btn2.setOnClickListener { onChoiceSelected(btn2) }
        btn3.setOnClickListener { onChoiceSelected(btn3) }

        btnConfirm.setOnClickListener {
            moveToResultPage()
        }
    }

    // 문제 로딩
    private fun loadQuiz() {

        val dbManager = SpellDBManager(this)
        val db = dbManager.readableDatabase

        // currentQuizId에 해당하는 문제 1개 가져옴
        val cursor = db.rawQuery(
            "SELECT * FROM spelling_quiz WHERE id = ?",
            arrayOf(currentQuizId.toString())
        )

        // 풀 문제 없으면 최종 결과 페이지로 이동
        if (!cursor.moveToFirst()) {
            cursor.close()
            db.close()
            moveToFinalResult()
            return
        }

        // Q번호 (1~5 반복 표시)
        val qNumber = ((currentQuizId - 1) % 5) + 1
        tvQNumber.text = "Q$qNumber"

        // DB에서 문제, 선택지, 정답, 해설, 이미지 경로 추출
        tvQuestion.text = cursor.getString(cursor.getColumnIndexOrThrow("sentence"))

        btn1.text = cursor.getString(cursor.getColumnIndexOrThrow("choice1"))
        btn2.text = cursor.getString(cursor.getColumnIndexOrThrow("choice2"))
        btn3.text = cursor.getString(cursor.getColumnIndexOrThrow("choice3"))

        correctAnswer = cursor.getString(cursor.getColumnIndexOrThrow("correct"))
        correctExp = cursor.getString(cursor.getColumnIndexOrThrow("correct_exp"))
        incorrectExp = cursor.getString(cursor.getColumnIndexOrThrow("incorrect_exp"))
        imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path")) ?: ""


        // assets 폴더 내 이미지 시용해서 이미지 가져오기
        if (imagePath.isNotBlank()) {
            Glide.with(this)
                .load("file:///android_asset/images/$imagePath")
                .into(imgExample)
        } else {
            imgExample.setImageDrawable(null)
        }

        resetChoiceButtons() // 선택지 ui 초기화
        disableConfirmButton() // 확인 버튼 비활성화

        imgDog.visibility = View.GONE // 강아지 안보이게
        selectedAnswer = ""

        cursor.close()
        db.close()
    }

    // 선택지 클릭했을 때
    private fun onChoiceSelected(btn: MaterialButton) {
        resetChoiceButtons()

        // ui 변화
        btn.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.choice_selected_bg))
        btn.setTextColor(getColor(R.color.choice_text_selected))

        btn.strokeColor = ColorStateList.valueOf(getColor(R.color.choice_text_selected))
        btn.strokeWidth = 2

        selectedAnswer = btn.text.toString()
        enableConfirmButton()
    }

    // 버튼들 초기화 상태로 만들기
    private fun resetChoiceButtons() {
        choiceButtons.forEach {
            it.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.spell_defalut_bg))
            it.strokeColor =
                ColorStateList.valueOf(getColor(R.color.spell_default_stroke))
            it.strokeWidth = 2
            it.setTextColor(getColor(R.color.spell_defalut_text))
        }
        selectedAnswer = "" // 선택했던 데이터 지움
    }

    // 확인 버튼 활성화
    private fun enableConfirmButton() {
        btnConfirm.isEnabled = true

        btnConfirm.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.choice_text_selected))
        btnConfirm.setTextColor(getColor(R.color.confirm_text_active))
        btnConfirm.strokeWidth = 0

        imgDog.visibility = View.VISIBLE
    }

    // 확인 버튼 비활성화
    private fun disableConfirmButton() {
        btnConfirm.isEnabled = false // 눌러도 반응 없음

        btnConfirm.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.spell_defalut_bg))
        btnConfirm.setTextColor(getColor(R.color.spell_defalut_text))
        btnConfirm.strokeColor =
            ColorStateList.valueOf(getColor(R.color.spell_default_stroke))
        btnConfirm.strokeWidth = 2

        imgDog.visibility = View.GONE
    }


    // 결과 페이지로 이동
    private fun moveToResultPage() {
        val isCorrect = selectedAnswer == correctAnswer

        totalCount++
        if (isCorrect) correctCount++

        FirestoreLevelStore().addSolved1(
            onSuccess = { _, _ -> },
            onFail = { e -> Log.e("SPELL_LEVEL", "fail", e) }
        )

        // 틀리면 오답 저장
        if (!isCorrect) {
            FirestoreWrongNote().addWrong(
                type = "spell",
                quizId = currentQuizId,
                userAnswer = selectedAnswer
            )
        }

        // 상태 증가
        currentQuizId++

        // 파이어베이스에 다음 문제 ID만 저장
        progressStore.saveSpellNextQuizId(currentQuizId)

        val qNumber = ((currentQuizId - 2) % 5) + 1
        // Q가 5번이면 무조건 세트 종료
        val isEndOfSet = ((currentQuizId - 1) % SET_SIZE) == 0

        val intent = Intent(this, SpellResultActivity::class.java).apply {
            putExtra("isCorrect", isCorrect)
            putExtra("sentence", tvQuestion.text.toString())
            putExtra("correctAnswer", correctAnswer)
            putExtra("correct_exp", correctExp)
            putExtra("incorrect_exp", incorrectExp)
            putExtra("image_path", imagePath)
            putExtra("quiz_id", currentQuizId - 1)
            putExtra("isEndOfPart", isEndOfSet)
        }

        startActivityForResult(intent, REQ_RESULT)
    }

    // 결과 복귀 (정답 페이지 -> 퀴즈 페이지)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_RESULT && resultCode == RESULT_OK) {
            val isEnd = data?.getBooleanExtra("isEndOfPart", false) ?: false
            if (isEnd) moveToFinalResult()
            else loadQuiz()
        }
    }

    // 최종 결과 페이지 이동
    private fun moveToFinalResult() {
        startActivity(
            Intent(this, SpellFinalResultActivity::class.java).apply {
                putExtra("totalSCount", totalCount)
                putExtra("correctSCount", correctCount)
            }
        )

        finish()
    }
}