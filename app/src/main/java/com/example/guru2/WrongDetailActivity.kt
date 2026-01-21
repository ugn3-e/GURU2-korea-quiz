package com.example.guru2

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WrongDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = intent.getStringExtra("quiz_type") ?: "slang"
        val quizId = intent.getIntExtra("quiz_id", -1)

        if (type == "slang") {
            setContentView(R.layout.activity_wrong_detail_slang)

            val quiz = SlangDBManager(this).getQuizById(quizId) ?: return

            findViewById<TextView>(R.id.tvSlangWord).text = quiz.slangWord
            findViewById<TextView>(R.id.tvExplanation).text = quiz.explanation
            findViewById<TextView>(R.id.tvNotice).text = quiz.notice

            val img = findViewById<ImageView>(R.id.ivExample)
            val resId = resources.getIdentifier(quiz.exampleImage, "drawable", packageName)
            if (resId != 0) img.setImageResource(resId)

            findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        } else {
            setContentView(R.layout.activity_wrong_detail_spelling)

            val quiz = DBManager(this).getQuizById(quizId) ?: return

            findViewById<TextView>(R.id.tvCorrect).text = quiz.correct
            findViewById<TextView>(R.id.tvSentence).text = quiz.sentence
            findViewById<TextView>(R.id.tvIncorrectExp).text = quiz.incorrect_exp

            findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
        }
    }
}
