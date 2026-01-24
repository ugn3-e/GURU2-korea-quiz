package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.OnBackPressedCallback
import com.example.guru2.fire.FirestoreProgress
import com.google.android.material.button.MaterialButton

class SlangFinalResultActivity : AppCompatActivity() {

    private lateinit var sSummary: TextView
    private lateinit var btnKeep: MaterialButton
    private lateinit var btnHome: MaterialButton

    private val progressStore by lazy { FirestoreProgress() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slang_final_result)

        // 디미 유진_결과 요약 텍스트
        sSummary = findViewById<TextView>(R.id.SlangSummary)

        // 디미 유진_이어서 학습하기 버튼
        btnKeep = findViewById(R.id.btnQKeep)
        btnKeep.isEnabled = false

        // 디미 유진_홈으로 돌아가기 버튼
        btnHome = findViewById(R.id.btnHome)

//        val total = intent.getIntExtra("totalCount", 0)
//        val correct = intent.getIntExtra("correctCount", 0)

        // 디미 유진_퀴즈 결과 요약 메세지
        sSummary.text = """
            수고하셨습니다!
            
            틀린 문제는 복습하기로
            한 번 더 학습할 수 있습니다!
        """.trimIndent()

        progressStore.loadSlangNextQuizId(
            onResult = { nextId ->
                val nextQuiz = try {
                    val slangDb = SlangDBManager(this)
                    slangDb.getQuizById(nextId)
                } catch (e: Exception) {
                    null
                }

                // 문제 다 풀었을 때 토스트 문구
                if (nextQuiz == null) {
                    runOnUiThread {
                        Toast.makeText(this, "모든 신조어 학습을 완료하였습니다!", Toast.LENGTH_SHORT).show()
                    }
                    btnKeep.isEnabled = false
                    return@loadSlangNextQuizId
                }
                else{
                    // ⭐ 다음 문제가 있을 때만 활성화
                    btnKeep.isEnabled = true
                }
            },
            onError = {
                Toast.makeText(this, "진행 정보를 불러올 수 없어요.", Toast.LENGTH_SHORT).show()
            }
        )

        // 디미 유진_이어서 학습하기 버튼
        // ⭐ 클릭 시에는 그냥 Quiz로 이동만
        btnKeep.setOnClickListener {

            // ⭐⭐⭐ 핵심 추가: Firestore 세트 상태 초기화
//            progressStore.saveSlangProgress(
//                nextQuizId = -1,   // 값은 Quiz에서 다시 덮어씀
//                solvedInSet = 0
//            )

            startActivity(
                Intent(this, SlangQuizActivity::class.java).apply {
                    //putExtra("continue", true)
                }
            )
            finish()
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