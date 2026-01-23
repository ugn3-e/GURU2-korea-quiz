package com.example.guru2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.view.MenuItem
import android.widget.Button
import android.widget.Toolbar

class SavedContentActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 레이아웃 설정(합치기)
        setContentView(R.layout.activity_saved_content)

        // 툴바
        val mainToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        setSupportActionBar(mainToolbar)

        // DB 가져오기
        val spellDbManager = SpellDBManager(this)
        val store = com.example.guru2.fire.FirestoreSavedContent()

        // 🔹 홈으로 이동 버튼
        val btnGoHome = findViewById<Button>(R.id.btnGoHome)
        btnGoHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            finish()
        }

        store.loadSpell(
            onResult = { savedList ->
                // Firestore에서 받은 quizId로 로컬 DB에서 상세 가져오기
                val quizzes = savedList.mapNotNull { item ->
                    val q = spellDbManager.getQuizById(item.quizId) ?: return@mapNotNull null
                    // Firestore의 savedDate를 화면에 쓰려면 saved_date를 덮어쓴 복사본 생성
                    q.copy(saved_date = item.savedDate, is_saved = 1)
                }

                android.util.Log.d("DB_CHECK", "Firestore 저장 데이터 개수: ${quizzes.size}")

                if (quizzes.isNotEmpty()) {
                    val grouped = quizzes.groupBy { it.saved_date ?: "Unknown" }
                    val displayList = grouped.map { (date, list) ->
                        DailySection(date, list)
                    }.reversed()

                    val mainRv = findViewById<RecyclerView>(R.id.rv_main_vertical)
                    mainRv.layoutManager = LinearLayoutManager(this)
                    mainRv.adapter = SavedDailyDBManager(displayList)
                } else {
                    android.util.Log.d("DB_CHECK", "저장된 데이터가 하나도 없습니다.")
                }
            },
            onFail = { e ->
                android.util.Log.e("DB_CHECK", "Firestore 불러오기 실패: ${e.message}")
            }
        )
    }
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                val intent = Intent(this, SurveyActivity::class.java)
                startActivity(intent)
                return true
            }

            // 디미 유진_마이페이지
            R.id.action_mypage -> {
                startActivity(Intent(this, com.example.guru2.mypage.MyPageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}