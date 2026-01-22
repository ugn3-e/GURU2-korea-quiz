package com.example.guru2

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guru2.auth.AuthRepository
import com.example.guru2.auth.SQLiteAuthDataSource
import com.example.guru2.fire.FirestoreProgress

class SpellQuizActivity : AppCompatActivity() {

    // 디미 유진_유저 아이디
    private var userId: Long = -1L

    lateinit var spellDbManager: SpellDBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var QuizText: TextView
    lateinit var ivQuizImg: ImageView // 유빈_추가 (이미지 연결)
    lateinit var btnChoice1: Button
    lateinit var btnChoice2: Button
    lateinit var btnChoice3: Button
    lateinit var btnSub: Button
    lateinit var QuizNum: TextView

    // 정답/오답 판단 변수
    var correctAnswer = ""
    var correct_exp = ""
    var incorrect_exp = ""
    //var explanation = ""
    //var source = ""
    // 퀴즈 번호
    var currentQuizId = 0
    var selectedAnswer = ""
    var quizCount = 1
    var quizId = 0

    // 전체 결과 누적 변수 // ☑️ 추가
    var totalSCount = 0
    var correctSCount = 0
    val PREF_LAST_OFFSET = "last_quiz_offset" // 이어서 학습

    var setCorrectCount = 0 // 5문제만 계산

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews() // ✅ 추가

        //DB 연결
        spellDbManager = SpellDBManager(this)

        // 누적 값
        totalSCount = intent.getIntExtra("totalSCount", 0)
        correctSCount = intent.getIntExtra("correctSCount", 0)

        // 5문제만 계산
        setCorrectCount = intent.getIntExtra("setCorrectCount", 0)

        // 제출 버튼 초기 상태
        btnSub.isEnabled = false
        btnSub.setBackgroundColor(getColor(R.color.choice_default))

        // 이어서 학습하기 불러오기
        quizId = intent.getIntExtra("quiz_id", -1)
        quizCount = intent.getIntExtra("quiz_count", 1)


        // 이어서 학습하기
        val progressStore = FirestoreProgress()

        if (quizId == -1) {
            progressStore.loadSpellNextQuizId(
                onResult = { nextId ->
                    quizId = nextId
                    QuizNum.text = "Q$quizCount"
                    loadNextQuiz()
                },
                onError = {
                    // 실패 시 안전하게 1번부터
                    quizId = 1
                    QuizNum.text = "Q$quizCount"
                }
            )
        } else {
            loadNextQuiz()
        }

        setClickListeners()

        // 유빈_추가 (퀴즈 접속하면 뒤로 가기 -> 막기)
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

    private fun initViews() {
        //view 연결
        QuizText = findViewById<TextView>(R.id.QuizText)
        QuizNum = findViewById<TextView>(R.id.QuizNum)
        ivQuizImg = findViewById<ImageView>(R.id.imageView)
        btnChoice1 = findViewById<Button>(R.id.btnChoice1)
        btnChoice2 = findViewById<Button>(R.id.btnChoice2)
        btnChoice3 = findViewById<Button>(R.id.btnChoice3)
        btnSub = findViewById<Button>(R.id.btnSub)
    }

    // 선택지 초기 상태
    private fun selectChoiceButton(selected: Button) {
        val defaultColor = getColor(R.color.choice_default)
        val selectedColor = getColor(R.color.choice_selected)

        btnChoice1.setBackgroundColor(defaultColor)
        btnChoice2.setBackgroundColor(defaultColor)
        btnChoice3.setBackgroundColor(defaultColor)

        selected.setBackgroundColor(selectedColor)
    }

    private fun enableSubmitButton() {
        btnSub.isEnabled = true
        btnSub.setBackgroundColor(getColor(R.color.choice_selected))
    }
    private fun setClickListeners() {
        //보기 선택
        btnChoice1.setOnClickListener {
            selectedAnswer = btnChoice1.text.toString()
            selectChoiceButton(btnChoice1)
            enableSubmitButton()
        }
        btnChoice2.setOnClickListener {
            selectedAnswer = btnChoice2.text.toString()
            selectChoiceButton(btnChoice2)
            enableSubmitButton()
        }
        btnChoice3.setOnClickListener {
            selectedAnswer = btnChoice3.text.toString()
            selectChoiceButton(btnChoice3)
            enableSubmitButton()
        }

        //ResultActivity1로 이동
        btnSub.setOnClickListener {
            val isCorrect = selectedAnswer == correctAnswer

            // 디미 유진_오답으로 이동
            if (!isCorrect) {
                WrongDBManager(this)
                    .saveSpellingWrong(this, currentQuizId, selectedAnswer)
            }

            // 디미 유진_레벨, solved_count +1
            val userId = getUserId()
            if (userId != -1L) {
                AuthRepository(SQLiteAuthDataSource(this))
                    .increaseSolvedCount(userId)
            } else {
                Log.e("LEVEL_CHECK", "SpellingQuiz userId == -1L, 증가 실패")
            }

            // 결과 누적 // ☑️ 추가
            totalSCount++ // 최종 결과
            if (isCorrect) {
                correctSCount++ // 전체 누적 맞힌 수
                setCorrectCount++ // 세트 전용 맞힌 수
            }

            if (quizId % 5 == 0) {
                val progressStore = FirestoreProgress()
                progressStore.saveSpellNextQuizId(
                    nextQuizId = quizId + 1,
                    onSuccess = { Log.d("FIRE_SAVE", "spell nextQuizId saved = ${quizId + 1}") },
                    onFail = { e -> Log.e("FIRE_SAVE", "spell save failed", e) }
                )
            }

            val intent = Intent(this, SpellResultActivity::class.java)

            intent.putExtra("quiz_count", quizCount) // ☑️ Q 번호
            intent.putExtra("quiz_id", currentQuizId)
            intent.putExtra("is_correct", isCorrect)
            intent.putExtra("isCorrect", isCorrect)
            intent.putExtra("sentence", QuizText.text.toString())
            intent.putExtra("correct", correctAnswer)
            intent.putExtra("correct_exp", correct_exp)
            intent.putExtra("incorrect_exp", incorrect_exp)
            intent.putExtra("selected_answer", selectedAnswer)

            // 누적값 전달 // ☑️ 추가
            intent.putExtra("totalSCount", totalSCount)
            intent.putExtra("correctSCount", correctSCount)

            // 다음 문제 준비
            quizCount++
            quizId++

            // 맞힌 수
            intent.putExtra("setCorrectCount", setCorrectCount)

            startActivity(intent)
        }
    }


    fun loadNextQuiz() {
        val db = spellDbManager.readableDatabase
        // Q1~5 반복 계산
        val qNumber = ((quizId - 1) % 5) + 1
        QuizNum.text = "Q$qNumber"

        // ORDER BY id ASC LIMIT 1 OFFSET ?
        // 순서대로 문제 출력
        // DB에서 문제 1개 가져오기
        val cursor = db.rawQuery( "SELECT * FROM spelling_quiz WHERE id = ?",
            arrayOf(quizId.toString()) // 중복 문제 해결 -> id로 가져오기
        )

        if(cursor.moveToFirst()) {
            // DB의 실제 id를 가져오기
            currentQuizId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))


            QuizText.text = cursor.getString(
                cursor.getColumnIndexOrThrow("sentence"))

            btnChoice1.text = cursor.getString(
                cursor.getColumnIndexOrThrow("choice1"))

            btnChoice2.text = cursor.getString(
                cursor.getColumnIndexOrThrow("choice2"))

            btnChoice3.text = cursor.getString(
                cursor.getColumnIndexOrThrow("choice3"))

            correctAnswer = cursor.getString(
                cursor.getColumnIndexOrThrow("correct"))

//              explanation = cursor.getString(
//                  cursor.getColumnIndexOrThrow("explanation"))
            correct_exp = cursor.getString(
                cursor.getColumnIndexOrThrow("correct_exp"))

            incorrect_exp = cursor.getString(
                cursor.getColumnIndexOrThrow("incorrect_exp"))

            // DB 필드 연결
            val imgPath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"))


            if (!imgPath.isNullOrEmpty()) {
                com.bumptech.glide.Glide.with(this)
                    .load("file:///android_asset/images/$imgPath")
                    .into(ivQuizImg)
            } else {
                // 이미지가 없는 경우 기본 이미지
                ivQuizImg.setImageResource(R.drawable.ic_launcher_foreground)
            }

            //source = cursor.getString(
            //cursor.getColumnIndexOrThrow("source"))

            selectChoiceButton(btnChoice1)
            btnChoice1.setBackgroundColor(getColor(R.color.choice_default))
            btnChoice2.setBackgroundColor(getColor(R.color.choice_default))
            btnChoice3.setBackgroundColor(getColor(R.color.choice_default))

            btnSub.isEnabled = false
            btnSub.setBackgroundColor(getColor(R.color.choice_default))

            selectedAnswer = "";
        } else {
            // 문제 다 풀었을 때
            moveToSpellFinalResultPage()
        }

        cursor.close()
        db.close()

    }

    // 퀴즈 완료 화면으로 이동 // ☑️ 추가
    private fun moveToSpellFinalResultPage() {
        val intent = Intent(this, SpellFinalResultActivity::class.java).apply {
            putExtra("totalSCount", totalSCount)
            putExtra("correctSCount", correctSCount)
            putExtra("setCorrectCount", setCorrectCount)
        }
        startActivity(intent)
        finish()
    }

    // 디미 유진_slang과 달리 새로운 페이지를 호출하는 방식이라 문제 개수 증가 코드가 다름
    private fun getUserId(): Long {
        return getSharedPreferences("auth", MODE_PRIVATE)
            .getLong("user_id", -1L)
    }
}