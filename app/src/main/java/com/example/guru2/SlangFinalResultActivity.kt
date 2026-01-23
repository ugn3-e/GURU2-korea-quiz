package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.guru2.fire.FirestoreProgress
import com.google.android.material.button.MaterialButton

class SlangFinalResultActivity : AppCompatActivity() {

    private lateinit var sSummary: TextView
    private lateinit var btnKeep: MaterialButton
    private lateinit var btnHome: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slang_final_result)

        // 디미 유진_결과 요약 텍스트
        sSummary = findViewById<TextView>(R.id.SlangSummary)

        // 디미 유진_이어서 학습하기 버튼
        btnKeep = findViewById(R.id.btnQKeep)

        // 디미 유진_홈으로 돌아가기 버튼
        btnHome = findViewById(R.id.btnHome)

        val total = intent.getIntExtra("totalCount", 0)
        val correct = intent.getIntExtra("correctCount", 0)

        // 디미 유진_퀴즈 결과 요약 메세지
        sSummary.text = """
            오늘의 문제 결과!
            ( $correct / $total )
            틀린 문제는 복습하기로
            한 번 더 학습할 수 있습니다!
        """.trimIndent()

        // 디미 유진_이어서 학습하기 버튼
        btnKeep.setOnClickListener {
            // 로그인 때 저장해둔 user_id 꺼내오기 (너희 앱 구조 기준)
//            val userId = getSharedPreferences("auth", MODE_PRIVATE)
//                .getLong("user_id", -1L)

            val progressStore = FirestoreProgress()
            progressStore.loadSlangNextQuizId(
                onResult = { nextId ->
                    // 로컬 DB에 다음 문제가 있는지 확인
                    val slangDb = SlangDBManager(this)
                    val nextQuiz = slangDb.getQuizById(nextId)

                    // 다음 문제가 없으면 → Toast만
                    if (nextQuiz == null) {
                        Toast.makeText(
                            this,
                            "모든 학습을 완료하였습니다!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@loadSlangNextQuizId
                    }
                    val intent = Intent(this, SlangQuizActivity::class.java).apply {
                        putExtra("startQuizId", nextId)   // 다음에 풀 문제
                    }
                    startActivity(intent)
                    finish()
                },
                onError = {
                    val intent = Intent(this, SlangQuizActivity::class.java).apply {
                        putExtra("startQuizId", 1)
                    }
                    startActivity(intent)
                    finish()
                }
            )
        }

        // 디미 유진_(수정) MainActivity로 이동 (뒤로 가기 눌러도 이전 퀴즈 화면으로 돌아가지 않음)
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()

        }

        // 디미 유진_뒤로가기 막기
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(
                        this@SlangFinalResultActivity,
                        "이어 학습하기 버튼이나 \n홈 버튼을 눌러주세요!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }
}