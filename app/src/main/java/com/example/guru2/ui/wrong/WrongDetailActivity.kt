package com.example.guru2.ui.wrong

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.DBManager
import com.example.guru2.R
import com.example.guru2.SlangDBManager

class WrongDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrong_detail)

        val quizId = intent.getIntExtra("quiz_id", -1)
        val quizType = intent.getStringExtra("quiz_type") ?: "slang"

        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        val tvExplanation = findViewById<TextView>(R.id.tvExplanation)
        val tvNotice = findViewById<TextView>(R.id.tvNotice)
        val imgExample = findViewById<ImageView>(R.id.imgExample)

        if (quizType == "slang") {
            val quiz = SlangDBManager(this).getQuizById(quizId)

            tvQuestion.text = quiz?.question
            tvExplanation.text = quiz?.explanation
            tvNotice.text = quiz?.notice

            quiz?.exampleImage?.let {
                val resId = resources.getIdentifier(it, "drawable", packageName)
                if (resId != 0) {
                    imgExample.setImageResource(resId)
                    imgExample.visibility = ImageView.VISIBLE
                }
            }
        } else {
            val quiz = DBManager(this).getQuizById(quizId)

            tvQuestion.text = quiz?.sentence
            tvExplanation.text = quiz?.correct_exp
            tvNotice.text = quiz?.incorrect_exp
            imgExample.visibility = ImageView.GONE
        }
    }
}
