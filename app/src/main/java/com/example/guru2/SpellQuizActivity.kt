package com.example.guru2

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SpellQuizActivity : AppCompatActivity() {
    lateinit var spellDbManager: SpellDBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var QuizText: TextView
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

    // 전체 결과 누적 변수 // ☑️ 추가
    var totalSCount = 0
    var correctSCount = 0

    // 퀴즈 수 5개 제한 변수 // ☑️ 추가
    val quizLimit = 5
    val prefName = "spell_quiz_pref"
    val lastOffset = "last_quiz_offset"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 마지막 퀴즈 위치 불러오기 // ☑️추가
        val pref = getSharedPreferences(prefName, MODE_PRIVATE)
        val lastOffset = pref.getInt(lastOffset, 0)

        // 마지막으로 푼 퀴즈에서 시작 // ☑️ 추가
        currentQuizId = lastOffset
        quizCount = 1

        // 누적 값
        quizCount = intent.getIntExtra("quiz_count", 1)
        totalSCount = intent.getIntExtra("totalSCount", 0)
        correctSCount = intent.getIntExtra("correctSCount", 0)

        initViews() // ✅ 추가

        //DB 연결
        spellDbManager = SpellDBManager(this)

        //초기화
        QuizNum.text = "Q$quizCount" // Q 번호
        loadNextQuiz() //  ✅ 추가

        setClickListeners()
    }

    private fun initViews() {
        //view 연결
        QuizText = findViewById<TextView>(R.id.QuizText)
        QuizNum = findViewById<TextView>(R.id.QuizNum)
        btnChoice1 = findViewById<Button>(R.id.btnChoice1)
        btnChoice2 = findViewById<Button>(R.id.btnChoice2)
        btnChoice3 = findViewById<Button>(R.id.btnChoice3)
        btnSub = findViewById<Button>(R.id.btnSub)
    }

    private fun setClickListeners() {
        //보기 선택
        btnChoice1.setOnClickListener {
            selectedAnswer = btnChoice1.text.toString()
        }
        btnChoice2.setOnClickListener {
            selectedAnswer = btnChoice2.text.toString()
        }
        btnChoice3.setOnClickListener {
            selectedAnswer = btnChoice3.text.toString()
        }

        //ResultActivity1로 이동
        btnSub.setOnClickListener {
            // 답을 선택했을 때만 다음 문제로 넘어가기 // ☑️ 추가
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, "보기를 선택하세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isCorrect = selectedAnswer == correctAnswer

            // 결과 누적 // ☑️ 추가
            totalSCount++ // 최종 결과
            if (isCorrect) correctSCount++ // 맞힌 문제 수

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
            //currentQuizId++
            quizCount++

            // 5문제 풀었을 때 종료 // ☑️ 추가
            if (quizCount > quizLimit) {
                // 마지막 퀴즈 위치 저장
                val pref = getSharedPreferences(prefName, MODE_PRIVATE)
                pref.edit().putInt(lastOffset, currentQuizId).apply()

                moveToSpellFinalResultPage()
                return@setOnClickListener
            }

            startActivity(intent)
        }
    }


    fun loadNextQuiz() {
        val db = spellDbManager.readableDatabase

        // ORDER BY id ASC LIMIT 1 OFFSET ?
        // 순서대로 문제 출력
        // DB에서 문제 1개 가져오기
        val cursor = db.rawQuery("SELECT * FROM spelling_quiz ORDER BY id ASC LIMIT 1 OFFSET ?",
             arrayOf((quizCount - 1).toString()) // 중복 문제 해결 -> id로 가져오기
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


            //source = cursor.getString(
            //cursor.getColumnIndexOrThrow("source"))

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
        }
        startActivity(intent)
        finish()
    }
}