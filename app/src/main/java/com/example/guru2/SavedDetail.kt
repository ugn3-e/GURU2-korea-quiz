package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class SavedDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_saved_detail)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val tvTitle = findViewById<TextView>(R.id.saved_title)
        val ivImg = findViewById<ImageView>(R.id.saved_img)
        val tvSentence = findViewById<TextView>(R.id.saved_sentence)
        val tvExp = findViewById<TextView>(R.id.saved_exp)
        val btnHome = findViewById<Button>(R.id.button2)

        // Intent 데이터 받기
        val quizId = intent.getIntExtra("quiz_id", -1)

        if(quizId != -1) {
            val dbManager = DBManager(this)
            val quiz = dbManager.getQuizById(quizId)

            quiz?.let {
                tvTitle.text = it.source ?: "출처 정보 없음"

                tvSentence.text = it.sentence.replace("____", it.correct)

                tvExp.text = it.correct_exp

                ivImg.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }

        // 홈으로 가기
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //?
            startActivity(intent)
            finish()
        }
    }
}