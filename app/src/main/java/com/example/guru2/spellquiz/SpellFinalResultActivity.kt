package com.example.guru2.spellquiz

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.guru2.R
import com.example.guru2.fire.FirestoreProgress
import com.example.guru2.home.MainActivity
import com.google.android.material.button.MaterialButton

class SpellFinalResultActivity : AppCompatActivity() {

    private lateinit var tvSummary: TextView // 결과 요약
    private lateinit var btnKeep: MaterialButton // 이어 풀기 버튼
    private lateinit var btnHome: MaterialButton // 홈으로 이동 버튼

    // 파이어베이스에 저장된 사용자의 퀴즈 진행도를 관리
    private val progressStore by lazy { FirestoreProgress() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spell_final_result)

        // View
        tvSummary = findViewById(R.id.SpellSummary)
        btnKeep = findViewById(R.id.btnQKeep)
        btnHome = findViewById(R.id.btnHome)

        // 기본 비활성화
        btnKeep.isEnabled = false

        // 내가 푼 문제 결과
        val total = intent.getIntExtra("totalSCount", 0) // 전체 푼 문제 수
        val correct = intent.getIntExtra("correctSCount", 0) // 맞춘 문제 수

        // 화면 문구 설정
        tvSummary.text = """
            수고하셨습니다!
            
            틀린 문제는 복습하기로
            한 번 더 학습할 수 있습니다!
        """.trimIndent()

        // 다음 문제 존재 여부 확인
        // 파이어베이스에서 다음에 풀어야 할 Quiz ID를 가져옴
        // 로컬 SQLite DB에 실제로 해당 ID의 데이터가 존재하는지 검사
        progressStore.loadSpellNextQuizId(
            onResult = { nextId ->
                // nextId가 spelling_quiz.db에 있는지 확인
                val nextQuiz = try {
                    val db = SpellDBManager(this)
                    // use 블록 -> 사용 후 DB와 Cursor가 자동으로 닫히도록 처리
                    db.readableDatabase.use { database ->
                        val cursor = database.rawQuery(
                            "SELECT id FROM spelling_quiz WHERE id = ?",
                            arrayOf(nextId.toString())
                        )
                        val exists = cursor.moveToFirst()
                        cursor.close()
                        exists // 데이터 존재 여부 반환
                    }
                } catch (e: Exception) {
                    false
                }

                if (!nextQuiz) {
                    // 더 이상 풀 문제가 없는 경우
                    Toast.makeText(
                        this,
                        "모든 맞춤법 학습을 완료하였습니다! \n설문조사를 통해 의견을 남겨주세요!",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnKeep.isEnabled = false // 버튼 비활성화 유지
                } else {
                    // 다음 문제가 있는 경우 -> 다음 학습하기 활성화
                    btnKeep.isEnabled = true
                }
            },
            onError = {
                Toast.makeText(
                    this,
                    "진행 정보를 불러올 수 없어요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        // 이어서 학습하는 경우
        btnKeep.setOnClickListener {
            // 다시 퀴즈 화면(SpellQuizActivity)으로 이동
            startActivity(
                Intent(this, SpellQuizActivity::class.java).apply {
                }
            )
            finish() // 현재 결과 화면 종료
        }

        // 홈으로 이동하는 경우
        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // 푸는 도중에 뒤로가기 막기
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(
                        this@SpellFinalResultActivity,
                        "이어 학습하기 또는 홈 버튼을 눌러주세요!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

    }
}