package com.example.guru2.saved

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2.R
import com.example.guru2.fire.FirestoreSavedContent
import com.example.guru2.home.MainActivity
import com.example.guru2.mypage.MyPageActivity
import com.example.guru2.spellquiz.SpellDBManager
import com.example.guru2.survey.SurveyActivity

// 저장된 콘텐츠 목록 화면
class SavedContentActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_content)

        // 툴바
        val mainToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
        setSupportActionBar(mainToolbar)

        // 로컬 DB (퀴즈 데이터)
        val spellDbManager = SpellDBManager(this)
        // Firestore (저장 기록)
        val store = FirestoreSavedContent()

        // 홈으로 이동 버튼
        val btnGoHome = findViewById<Button>(R.id.btnGoHome)
        btnGoHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        }

        // Firestore에서 저장된 콘텐츠 불러오기
        store.loadSpell(
            onResult = { savedList ->
                // quizId를 통해 로컬 DB에서 퀴즈 데이터 조회
                val quizzes = savedList.mapNotNull { item ->
                    val q = spellDbManager.getQuizById(item.quizId) ?: return@mapNotNull null
                    // Firestore의 savedDate를 화면에 쓰려면 saved_date를 덮어쓴 복사본 생성 필요함
                    q.copy(saved_date = item.savedDate, is_saved = 1)
                }

                Log.d("DB_CHECK", "Firestore 저장 데이터 개수: ${quizzes.size}")

                // 저장된 데이터가 있을 경우
                if (quizzes.isNotEmpty()) {
                    val grouped = quizzes.groupBy { it.saved_date ?: "Unknown" }
                    val displayList = grouped.map { (date, list) ->
                        DailySection(date, list)
                    }.reversed()

                    val mainRv = findViewById<RecyclerView>(R.id.rv_main_vertical)
                    mainRv.layoutManager = LinearLayoutManager(this)
                    mainRv.adapter = SavedDailyDBManager(displayList)
                } else {
                    Log.d("DB_CHECK", "저장된 데이터가 하나도 없습니다.")
                }
            },
            onFail = { e ->
                Log.e("DB_CHECK", "Firestore 불러오기 실패: ${e.message}")
            }
        )
    }

    // 상단 메뉴 연결
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 상단 메뉴 클릭
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_survey -> {
                val intent = Intent(this, SurveyActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_mypage -> {
                startActivity(Intent(this, MyPageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}