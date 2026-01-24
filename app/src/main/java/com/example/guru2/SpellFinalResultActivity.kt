package com.example.guru2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import androidx.activity.OnBackPressedCallback
import android.widget.Toast
import com.example.guru2.fire.FirestoreProgress

class SpellFinalResultActivity : AppCompatActivity() {

    private lateinit var tvSummary: TextView
    private lateinit var btnKeep: MaterialButton
    private lateinit var btnHome: MaterialButton

    // ⭐ Firestore 진행 상태
    private val progressStore by lazy { FirestoreProgress() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spell_final_result)

        // ================= 툴바 =================
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Quiz"

        // ================= View =================
        tvSummary = findViewById(R.id.SpellSummary)
        btnKeep = findViewById(R.id.btnQKeep)
        btnHome = findViewById(R.id.btnHome)

        // ⭐ 기본 비활성화
        btnKeep.isEnabled = false

        // ================= 결과 데이터 =================
        val total = intent.getIntExtra("totalSCount", 0)
        val correct = intent.getIntExtra("correctSCount", 0)

        tvSummary.text = """
            수고하셨습니다!
            
            틀린 문제는 복습하기로
            한 번 더 학습할 수 있습니다!
        """.trimIndent()

        // ================= ⭐ 다음 문제 존재 여부 확인 =================
        progressStore.loadSpellNextQuizId(
            onResult = { nextId ->
                val nextQuiz = try {
                    val db = SpellDBManager(this)
                    db.readableDatabase.use { database ->
                        val cursor = database.rawQuery(
                            "SELECT id FROM spelling_quiz WHERE id = ?",
                            arrayOf(nextId.toString())
                        )
                        val exists = cursor.moveToFirst()
                        cursor.close()
                        exists
                    }
                } catch (e: Exception) {
                    false
                }

                if (!nextQuiz) {
                    // ❌ 더 이상 풀 문제가 없음
                    Toast.makeText(
                        this,
                        "모든 맞춤법 학습을 완료하였습니다!",
                        Toast.LENGTH_SHORT
                    ).show()
                    btnKeep.isEnabled = false
                } else {
                    // ✅ 다음 문제가 있음 → 이어하기 활성화
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

        // ================= 이어서 학습 =================
        btnKeep.setOnClickListener {

            // ⭐⭐⭐ 핵심 추가: spell 세트 상태 초기화
//            progressStore.saveSpellProgress(
//                nextQuizId = -1,   // Quiz에서 다시 덮어씀
//                solvedInSet = 0
//            )

            startActivity(
                Intent(this, SpellQuizActivity::class.java).apply {
                    //putExtra("continue", true)   // slang과 동일
                }
            )
            finish()
        }

        // ================= 홈으로 =================
        btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // ================= 뒤로가기 막기 =================
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

    // ================= 메뉴 =================
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                startActivity(Intent(this, SurveyActivity::class.java))
                return true
            }
            R.id.action_mypage -> {
                startActivity(
                    Intent(this, com.example.guru2.mypage.MyPageActivity::class.java)
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
