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

class QuizActivity1 : AppCompatActivity() {
    lateinit var dbManager: DBManager
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

    // 전체 결과 누적 변수
    var totalSCount = 0
    var correctSCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        quizCount = intent.getIntExtra("quiz_count", 1)
        totalSCount = intent.getIntExtra("totalSCount", 0)
        correctSCount = intent.getIntExtra("correctSCount", 0)

        initViews() // ✅ 추가
        QuizNum.text = "Q$quizCount" // Q 번호

        //DB 연결
        dbManager = DBManager(this)

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
            val isCorrect = selectedAnswer == correctAnswer

            // 결과 누적 // ☑️ 추가
            totalSCount++ // 최종 결과
            if (isCorrect) correctSCount++ // 맞힌 문제 수

            val intent = Intent(this, ResultActivity1::class.java)

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

            startActivity(intent)
        }
    }


    fun loadNextQuiz() {
        val db = dbManager.readableDatabase

        // ORDER BY id ASC LIMIT 1 OFFSET ?
        // 순서대로 문제 출력
        // DB에서 문제 1개 가져오기
        val cursor = db.rawQuery("SELECT * FROM spelling_quiz ORDER BY id ASC LIMIT 1 OFFSET ?",
             arrayOf((quizCount - 1).toString()) // 중복 문제 해결 -> id로 가져오기
        )

        if(cursor.moveToFirst()) {
            // 문제 id 저장하기(문제 순서)
            currentQuizId = cursor.getInt(
                cursor.getColumnIndexOrThrow("id"))

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