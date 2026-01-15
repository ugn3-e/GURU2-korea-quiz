package com.example.guru2

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

    var correctAnswer = ""
    var explanation = ""
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

        //view 연결
        QuizText = findViewById<TextView>(R.id.QuizText)
        btnChoice1 = findViewById<Button>(R.id.btnChoice1)
        btnChoice2 = findViewById<Button>(R.id.btnChoice2)
        btnChoice3 = findViewById<Button>(R.id.btnChoice3)
        btnSub = findViewById<Button>(R.id.btnSub)

        //DB 연결
        dbManager = DBManager(this, "spelling_quiz", null, 1)
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

            explanation = cursor.getString(
                cursor.getColumnIndexOrThrow("explanation"))

            source = cursor.getString(
                cursor.getColumnIndexOrThrow("source"))
        }

        cursor.close()
        db.close()

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

        btnSub.setOnClickListener {
            if(selectedAnswer == correctAnswer){
                //나중에 Correct/inCorrect Activity로 이동
                Toast.makeText(this, "정답!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "오답", Toast.LENGTH_SHORT).show()
            }
        }
    }
}