package com.example.guru2

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

    var correctAnswer = ""
    var correct_exp = ""
    var incorrect_exp = ""
    //var explanation = ""
    var source = ""
    var selectedAnswer = ""


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
        dbManager = DBManager(this, "spelling_quiz.db", null, 2)

        loadNextQuiz() //  ✅ 추가

        setClickListeners()
    }

    private fun initViews() {
        //view 연결
        QuizText = findViewById<TextView>(R.id.QuizText)
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

            val intent = Intent(this, ResultActivity1::class.java)
            intent.putExtra("is_correct", isCorrect)
            intent.putExtra("sentence", QuizText.text.toString())
            intent.putExtra("correct", correctAnswer)
            intent.putExtra("correct_exp", correct_exp)
            intent.putExtra("incorrect_exp", incorrect_exp)
            intent.putExtra("selected_answer", selectedAnswer)

            startActivity(intent)
        }
    }


        fun loadNextQuiz() {
            val db = dbManager.readableDatabase

            //DB에서 문제 1개 가져오기
            val cursor = db.rawQuery("SELECT * FROM spelling_quiz ORDER BY RANDOM() LIMIT 1",
                null)

            if(cursor.moveToFirst()) {
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


                source = cursor.getString(
                    cursor.getColumnIndexOrThrow("source"))

                selectedAnswer = "";
            }

            cursor.close()
            db.close()

        }
    }