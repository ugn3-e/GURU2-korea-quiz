package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import android.content.res.ColorStateList
import com.example.guru2.fire.FirestoreLevelStore
import com.example.guru2.fire.FirestoreWrongNote
import com.example.guru2.fire.FirestoreProgress // ⭐ 추가
import com.google.firebase.auth.FirebaseAuth

class SpellQuizActivity : AppCompatActivity() {

    companion object {
        private const val REQ_RESULT = 1001
        private const val SET_SIZE = 5
    }

    // ================= View =================
    private lateinit var tvQNumber: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var imgExample: ImageView
    private lateinit var imgDog: ImageView

    private lateinit var btn1: MaterialButton
    private lateinit var btn2: MaterialButton
    private lateinit var btn3: MaterialButton
    private lateinit var btnConfirm: MaterialButton
    private lateinit var choiceButtons: List<MaterialButton>

    // ================= 상태 =================
    private var currentQuizId = 1        // 🔥 전역 문제 ID
    //private var solvedInSet = 0           // 🔥 이번 세트에서 푼 문제 수
    //private var displayQNumber = 0   // ⭐ UI 전용 (항상 1부터)

    private var selectedAnswer = ""

    private var correctAnswer = ""
    private var correctExp = ""
    private var incorrectExp = ""
    private var imagePath = ""

    private var totalCount = 0
    private var correctCount = 0

    // ⭐ Firestore Progress
    private val progressStore by lazy { FirestoreProgress() }

    // 🔥 slang과 동일한 SharedPreferences
//    private val pref by lazy {
//        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
//        getSharedPreferences("spell_quiz_$uid", MODE_PRIVATE)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spell_quiz)


        bindViews()
        setClickListeners()
//        // ================= 이어서 학습 복원 =================
//        currentQuizId = pref.getInt("currentQuizId", 1)
//        solvedInSet = pref.getInt("solvedInSet", 0)

        // ⭐ slang과 동일: 이어하기 여부
//        val isContinue = intent.getBooleanExtra("continue", false)
//
//        // ================= ⭐ 진행 상태 복원 =================
//        progressStore.loadSpellProgress(
//            onResult = { nextId, solved ->
//                currentQuizId = nextId
//                solvedInSet = if (isContinue) 0 else solved
//                //displayQNumber = 0   // ⭐⭐⭐ 추가
//
//                if (!isContinue && solvedInSet >= SET_SIZE) {
//                    moveToFinalResult()
//                } else {
//                    loadQuiz()
//                }
//            },
//            onError = {
//                // fallback (비로그인 / 네트워크 오류)
//                loadQuiz()
//            }
//        )
        // ✅ Firestore에서 다음 문제 ID만 복원
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

    // ================= View =================
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

    // ================= 클릭 =================
    private fun setClickListeners() {
        btn1.setOnClickListener { onChoiceSelected(btn1) }
        btn2.setOnClickListener { onChoiceSelected(btn2) }
        btn3.setOnClickListener { onChoiceSelected(btn3) }

        btnConfirm.setOnClickListener {
            moveToResultPage()
        }
    }

    // ================= 문제 로딩 =================
    private fun loadQuiz() {

//        // 🔥 세트 종료
//        if (solvedInSet >= SET_SIZE) {
//            moveToFinalResult()
//            return
//        }

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

        //displayQNumber++
        //tvQNumber.text = "Q$displayQNumber"

        // Q번호 (🔥 slang 동일)
        //tvQNumber.text = "Q${solvedInSet + 1}"
        // DB 기준 문제 번호 그대로 표시
        //tvQNumber.text = "Q$currentQuizId"

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
        imgDog.visibility = View.GONE
        selectedAnswer = ""

        cursor.close()
        db.close()
    }

    // ================= 선택지 =================
    private fun onChoiceSelected(btn: MaterialButton) {
        resetChoiceButtons()

        btn.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.choice_selected_bg))
        btn.setTextColor(getColor(R.color.choice_text_selected))

        btn.strokeColor = ColorStateList.valueOf(getColor(R.color.choice_text_selected))
        btn.strokeWidth = 2

        selectedAnswer = btn.text.toString()
        enableConfirmButton()
    }

    // 리셋
    private fun resetChoiceButtons() {
        choiceButtons.forEach {
            it.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.spell_defalut_bg))
            it.strokeColor =
                ColorStateList.valueOf(getColor(R.color.spell_default_stroke))
            it.strokeWidth = 2
            it.setTextColor(getColor(R.color.spell_defalut_text))
        }
        selectedAnswer = ""
    }

    private fun enableConfirmButton() {
        btnConfirm.isEnabled = true

        btnConfirm.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.choice_text_selected))
        btnConfirm.setTextColor(getColor(R.color.confirm_text_active))
        btnConfirm.strokeWidth = 0

        imgDog.visibility = View.VISIBLE
    }


    private fun disableConfirmButton() {
        btnConfirm.isEnabled = false

        btnConfirm.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.spell_defalut_bg))
        btnConfirm.setTextColor(getColor(R.color.spell_defalut_text))
        btnConfirm.strokeColor =
            ColorStateList.valueOf(getColor(R.color.spell_default_stroke))
        btnConfirm.strokeWidth = 2

        imgDog.visibility = View.GONE
    }


    // ================= 결과 이동 =================
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

        // 🔥 핵심: 상태 증가 + 저장
        //solvedInSet++
        currentQuizId++

//        pref.edit()
//            .putInt("currentQuizId", currentQuizId)
//            .putInt("solvedInSet", solvedInSet)
//            .apply()

        // 🔥 Firestore에는 다음 문제 ID만 저장
        progressStore.saveSpellNextQuizId(currentQuizId)

        val qNumber = ((currentQuizId - 2) % 5) + 1
        // ✅ Q가 5번이면 무조건 세트 종료
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

    // ================= 결과 복귀 =================
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_RESULT && resultCode == RESULT_OK) {
            val isEnd = data?.getBooleanExtra("isEndOfPart", false) ?: false
            if (isEnd) moveToFinalResult()
            else loadQuiz()
        }
    }

    // ================= 최종 결과 =================
    private fun moveToFinalResult() {
        startActivity(
            Intent(this, SpellFinalResultActivity::class.java).apply {
                putExtra("totalSCount", totalCount)
                putExtra("correctSCount", correctCount)
            }
        )

        // 🔥 다음 세트를 위해 초기화
//        pref.edit()
//            .putInt("solvedInSet", 0)
//            .apply()

        finish()
    }
}
