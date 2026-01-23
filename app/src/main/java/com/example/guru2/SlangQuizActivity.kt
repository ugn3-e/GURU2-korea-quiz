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
import com.google.android.material.button.MaterialButton
import android.content.res.ColorStateList
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.fire.FirestoreLevelStore
import com.example.guru2.fire.FirestoreProgress
import com.example.guru2.fire.FirestoreWrongNote
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide



class SlangQuizActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    // 디미 유진_결과 화면 -> 다음 문제 화면으로 넘어갈 때 확인을 위한 번호
    companion object {
        private const val REQ_RESULT = 1001
        private const val SET_SIZE = 5 // 🔥 일일 퀴즈 개수
    }

    // 디미 유진_유저 아이디 받기
    //private var userId: Long = -1L

    // 디미 유진_전체 결과 누적 변수
    private var totalCount = 0
    private var correctCount = 0

    // 디미 유진_Q 번호
    private var currentQuizId = 1  // 🔥 전역 문제 ID (DB 기준)
    private var solvedInSet = 0         // 🔥 이번 세트에서 푼 문제 수 (0~5)
    private lateinit var tvQNumber: TextView

    // 디미 유진_문제
    private lateinit var tvQuestion: TextView

    // 디미 유진_선택지 버튼
    // MaterialButton으로 변경
    private lateinit var btn1: MaterialButton
    private lateinit var btn2: MaterialButton
    private lateinit var btn3: MaterialButton
    private lateinit var btn4: MaterialButton
    private lateinit var choiceButtons: List<MaterialButton>

    // 디미 유진_상황 예시 이미지
    private lateinit var btnShowExample: TextView
    private lateinit var imgExample: ImageView
    private lateinit var dogImage: ImageView

    // 디미 유진_확인 버튼
    private lateinit var btnConfirm: MaterialButton

    // 디미 유진_선택지 값
    private var selectedAnswer = ""
    private lateinit var currentQuiz: SlangQuizData

    // 🔥 SharedPreferences
    // ===============================
    private val pref by lazy {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        getSharedPreferences("slang_quiz_$uid", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slang_quiz)

        // 툴바
        val mainToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        setSupportActionBar(mainToolbar)

        supportActionBar?.apply {
            title = "Quiz" // 타이틀 설정
            // 만약 뒤로가기 버튼이 필요하면
            // setDisplayHomeAsUpEnabled(true)
        }

        // 디미 유진_유저 아이디 연결
        //userId = intent.getLongExtra("user_id", -1L)

        // 이어서 학습하기
//        val startId = intent.getIntExtra("startQuizId", -1)
//        val progressStore = FirestoreProgress()
//
//        if (startId != -1) {
//            // Final → 이어하기
//            currentQuizId = startId
//            loadQuizFromDB()
//        } else {
//            // 홈 → 퀴즈 버튼
//            progressStore.loadSlangNextQuizId(
//                onResult = { nextId ->
//                    currentQuizId = nextId
//                    loadQuizFromDB()
//                },
//                onError = {
//                    currentQuizId = 1
//                    loadQuizFromDB()
//                }
//            )
//        }

        // ⭐ 핵심 1: startQuizId 우선 적용
        // =====================================================
        if (intent.hasExtra("startQuizId")) {
            currentQuizId = intent.getIntExtra("startQuizId", 1)
            solvedInSet = 0   // ⭐ 새 세트 시작
        } else {
            // 홈 → 자동 이어하기
            currentQuizId = pref.getInt("currentQuizId", 1)
            solvedInSet = pref.getInt("solvedInSet", 0)
        }
        // 디미 유진_View 연결
        tvQNumber = findViewById(R.id.QText)
        tvQuestion = findViewById(R.id.tvQuestion)

        btn1 = findViewById(R.id.btnChoice1)
        btn2 = findViewById(R.id.btnChoice2)
        btn3 = findViewById(R.id.btnChoice3)
        btn4 = findViewById(R.id.btnChoice4)

        btnShowExample = findViewById(R.id.btnShowExample)
        imgExample = findViewById(R.id.imgExample)
        dogImage = findViewById(R.id.DogImage)

        btnConfirm = findViewById(R.id.btnConfirm)

        choiceButtons = listOf(btn1, btn2, btn3, btn4)

        // 디미 유진_초기 UI 상태 (확인버튼, 상황 예시 이미지 X)
        resetChoiceButtons()
        //btnConfirm.isEnabled = false
        disableConfirmButton()
        imgExample.visibility = View.GONE

        // 디미 유진_선택지 클릭 이벤트
        btn1.setOnClickListener { onChoiceSelected(btn1) }
        btn2.setOnClickListener { onChoiceSelected(btn2) }
        btn3.setOnClickListener { onChoiceSelected(btn3) }
        btn4.setOnClickListener { onChoiceSelected(btn4) }

        // 디미 유진_상황 예시 이미지 보기
        btnShowExample.setOnClickListener {
            imgExample.visibility = View.VISIBLE
            dogImage.visibility = View.GONE
        }

        imgExample.setOnClickListener {
            imgExample.visibility = View.GONE
            dogImage.visibility = View.VISIBLE
        }

        // 디미 유진_정답 확인 -> 결과 화면 이동
        btnConfirm.setOnClickListener {
            moveToResultPage()
        }

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

        loadQuizFromDB()
    }

    // 메뉴 연결
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 디미 유진_(추가)마이페이지 추가
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                val intent = Intent(this, SurveyActivity::class.java)
                startActivity(intent)
                return true
            }

            // 디미 유진_마이페이지
            R.id.action_mypage -> {
                startActivity(Intent(this, com.example.guru2.mypage.MyPageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 디미 유진_DB에서 문제 1개 가져오기
    private fun loadQuizFromDB() {

        // 🔥 방어 코드 (혹시 꼬였을 경우)
        if (solvedInSet >= SET_SIZE) {
            moveToFinalResultPage()
            return
        }

        val dbManager = SlangDBManager(this)
        val quiz = dbManager.getQuizById(currentQuizId)

        // Q1~5만 반복 ☑️
        //val qNumber = ((currentQuizId - 1) % 5) + 1

        // 디미 유진_문제 다 풀었을 경우
        // >> (수정) 마지막 문제 안내 페이지로 연결
        if (quiz == null) {
            //Toast.makeText(this, "모든 문제를 풀었습니다 🎉", Toast.LENGTH_SHORT).show()
            moveToFinalResultPage()
            return
        }

        currentQuiz = quiz

        // 디미 유진_불러온 Q 번호
        //tvQNumber.text = "Q$qNumber"
        tvQNumber.text = "Q${solvedInSet + 1}"

        // 디미 유진_불러온 문제 & 선택지
        tvQuestion.text = quiz.question

        // 디미 유진_원본 선택지 로그
        Log.d("Clog", "Q${quiz.id} 원본 보기 = ${listOf(
            quiz.choice1, quiz.choice2, quiz.choice3, quiz.choice4
        )}")

        // 디미 유진_선택지 섞기
        setShuffledChoices(quiz)


        // 디미 유진_불러온 문제의 상황 예시 이미지
        // 🔥 Glide로 assets/slang_image 이미지 로딩
//        imgExample.visibility = View.GONE
//        val imgPath = quiz.exampleImage + ".png"
//
//        Glide.with(this)
//            .load("file:///android_asset/slang_image/$imgPath")
//            .into(imgExample)
        imgExample.visibility = View.GONE
        val rawImage = quiz.exampleImage
        val imgPath =
            if (!rawImage.contains(".")) "$rawImage.png" else rawImage

        Glide.with(this)
            .load("file:///android_asset/slang_image/$imgPath")
            .into(imgExample)



//        val imageResId = resources.getIdentifier(
//            quiz.exampleImage,
//            "drawable",
//            packageName
//        )
//
//        imgExample.visibility = View.GONE
//        if (imageResId != 0) {
//            imgExample.setImageResource(imageResId)
//        }

        // 디미 유진_선택지, 확인 버튼 초기화
        resetChoiceButtons()
        //btnConfirm.isEnabled = false
        disableConfirmButton()
    }


    // 디미 유진_선택지 클릭 처리
    // ======================= UI 제어 =======================

    private fun onChoiceSelected(selected: MaterialButton) {
        resetChoiceButtons()

        selected.strokeColor =
            ColorStateList.valueOf(getColor(R.color.button_selected_stroke))
        selected.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.choice_selected_bg
            ))
        selected.setTextColor(getColor(R.color.choice_text_selected)) // ⭐ 핵심

        selectedAnswer = selected.text.toString()
        enableConfirmButton()
    }

    private fun resetChoiceButtons() {
        choiceButtons.forEach {
            it.backgroundTintList =
                ColorStateList.valueOf(getColor(R.color.button_default_bg))
            it.strokeColor =
                ColorStateList.valueOf(getColor(R.color.button_default_stroke))
            it.setTextColor(getColor(R.color.button_text_default)) // ⭐ 추가
        }
        selectedAnswer = ""
    }

    private fun enableConfirmButton() {
        btnConfirm.isEnabled = true
        btnConfirm.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.confirm_active))

        btnConfirm.strokeColor =
            ColorStateList.valueOf(getColor(R.color.button_selected_stroke))

        btnConfirm.setTextColor(
            ColorStateList.valueOf(getColor(R.color.confirm_text_active))
        )
    }

    private fun disableConfirmButton() {
        btnConfirm.isEnabled = false
        btnConfirm.backgroundTintList =
            ColorStateList.valueOf(getColor(R.color.button_default_bg))

        btnConfirm.strokeColor =
            ColorStateList.valueOf(getColor(R.color.button_default_stroke))

        btnConfirm.setTextColor(
            ColorStateList.valueOf(getColor(R.color.button_text_default))
        )
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

        FirestoreLevelStore().addSolved1(
            onSuccess = { level, totalSolved ->
                Log.d(
                    "FIRE_LEVEL",
                    "레벨 업데이트 성공 (slang) → level=$level, solved=$totalSolved"
                )
            },
            onFail = { e ->
                Log.e("FIRE_LEVEL", "레벨 업데이트 실패 (slang)", e)
                Log.d("LEVEL_UID", "uid=${FirebaseAuth.getInstance().currentUser?.uid}")
            }
        )

        // 디미 유진_오답일 경우 현재 퀴즈 질문과 선택한 답 db에 저장하기
        if (!isCorrect) {
            //WrongDBManager(this) .saveSlangWrong(this, currentQuiz.id, selectedAnswer)

            // Firestore ☑️
            FirestoreWrongNote().addWrong(
                type = "slang",
                quizId = currentQuiz.id,
                userAnswer = selectedAnswer
            )
        }

        // 🔥 이번 세트에서 푼 문제 증가
        solvedInSet++
        currentQuizId++

        // 🔥 상태 저장 (앱 종료 대비)
        pref.edit()
            .putInt("currentQuizId", currentQuizId)
            .putInt("solvedInSet", solvedInSet)
            .apply()

        val isEndOfSet = solvedInSet == SET_SIZE

        // 디미 유진_유저 레벨, solvedCount +1
//        if (userId != -1L) {
//            val authRepo = AuthRepository(SQLiteAuthDataSource(this))
//            authRepo.increaseSolvedCount(userId)
//        } else {
//            Log.e("LEVEL_CHECK", "SlangQuiz userId == -1L, 증가 실패")
//        }

//        try {
//            FirestoreLevelStore().addSolved1(
//                onSuccess = { level, totalSolved ->
//                    Log.d("FIRE_LEVEL", "slang +1 -> totalSolved=$totalSolved, level=$level")
//                },
//                onFail = { e ->
//                    Log.e("FIRE_LEVEL", "slang update failed", e)
//                }
//            )
//        } catch (e: Exception) {
//            Log.e("FIRE_LEVEL", "slang auth null?", e)
//        }


        // 다음에 풀 문제 ID 저장 ☑️
//        val progressStore = FirestoreProgress()
//        progressStore.saveSlangNextQuizId(currentQuizId + 1)
//
//        val intent = Intent(this, SlangResultActivity::class.java).apply {
//            putExtra("isCorrect", isCorrect)
//            putExtra("explanation", currentQuiz.explanation)
//            putExtra("notice", currentQuiz.notice)
//            putExtra("exampleImage", currentQuiz.exampleImage)
//            putExtra("nextQuizId", currentQuizId + 1)
//            // 5문제마다 최종 결과 화면으로 이동 ☑️
//            //putExtra("isEndOfPart", totalCount % 5 == 0)
//            putExtra("isEndOfPart", isEndOfSet) // 🔥 핵심 판단 기준
//        }
//
//        startActivityForResult(intent, REQ_RESULT)
        // 🔥 Firestore에는 이미 증가된 ID 저장
        FirestoreProgress().saveSlangNextQuizId(currentQuizId)

        val intent = Intent(this, SlangResultActivity::class.java).apply {
            putExtra("isCorrect", isCorrect)
            putExtra("explanation", currentQuiz.explanation)
            putExtra("notice", currentQuiz.notice)
            putExtra("exampleImage", currentQuiz.exampleImage)
            putExtra("nextQuizId", currentQuizId)
            putExtra("isEndOfPart", isEndOfSet) // 🔥 최종 판단 기준
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
            else{
                loadQuizFromDB()
            }

//            currentQuizId =
//                data?.getIntExtra("nextQuizId", currentQuizId + 1)
//                    ?: (currentQuizId + 1)
//
//            loadQuizFromDB()
        }
    }

    // 디미 유진_최종 결과 화면 이동
    private fun moveToFinalResultPage() {

        // ✅ 1. 먼저 결과 화면으로 전달 (초기화 ❌)
        val intent = Intent(this, SlangFinalResultActivity::class.java).apply {
            putExtra("totalCount", totalCount)
            putExtra("correctCount", correctCount)
        }

        startActivity(intent)

        // ✅ 2. 그 다음에 다음 세트를 위한 초기화
        totalCount = 0
        correctCount = 0

        pref.edit()
            .putInt("solvedInSet", 0)
            .apply()

        finish()
    }
}