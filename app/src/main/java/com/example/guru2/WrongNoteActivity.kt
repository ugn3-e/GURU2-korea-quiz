package com.example.guru2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.guru2.fire.FirestoreWrongNote
import androidx.constraintlayout.widget.ConstraintSet

// 오답 노트 메인 화면 Activity
// 신조어 / 맞춤법 오답 탭 전환 및 초기화 기능 담당

class WrongNoteActivity : AppCompatActivity() {
    private var currentType: String = "slang"

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrong_note)

        // 툴바
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "오답" // 타이틀 설정
            // 만약 뒤로가기 버튼이 필요하면
            // setDisplayHomeAsUpEnabled(true)
        }

        // 디미 유진_상단 탭 버튼 (TextView 사용)
        val btnSlang = findViewById<TextView>(R.id.btnSlang)
        val btnGrammar = findViewById<TextView>(R.id.btnGrammar)

        // 디미 유진_오답 전체 삭제 버튼
        val btnClearWrong = findViewById<Button>(R.id.btnClearWrong)

        // 디미 유진_홈으로 돌아가기 버튼
        val btnGoHome = findViewById<Button>(R.id.btnGoHome)

        // 디미 유진_오답 목록을 보여줄 ViewPager
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // 디미 유진_ViewPager 어댑터 설정 (신조어 / 맞춤법 Fragment)
        viewPager.adapter = WrongPagerAdapter(this)

        // 디미 유진_사용자 스와이프로 페이지 이동 금지 -> 탭 버튼 클릭으로만 전환
        viewPager.isUserInputEnabled = false

        // 디미 유진_신조어 탭 클릭
        btnSlang.setOnClickListener {
            currentType = "slang"
            viewPager.setCurrentItem(0, false)
            selectTab(true)
        }

        // 디미 유진_맞춤법 탭 클릭
        btnGrammar.setOnClickListener {
            currentType = "spell"
            viewPager.setCurrentItem(1, false)
            selectTab(false)
        }

        // 디미 유진_오답 전체 삭제 버튼 클릭
        btnClearWrong.setOnClickListener { showClearDialog() }

        // 디미 유진_홈으로 돌아가기
        btnGoHome.setOnClickListener {
            finish()
        }

        // 디미 유진_최초 진입 시 신조어 탭 선택 상태로 설정
        selectTab(true)
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

    // 디미 유진_선택된 탭에 따라 텍스트 색상 변경
    private fun selectTab(isSlang: Boolean) {
        val btnSlang = findViewById<TextView>(R.id.btnSlang)
        val btnGrammar = findViewById<TextView>(R.id.btnGrammar)
        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.mainLayout)


        // 디미 유진_신조어 탭 선택 상태
        if (isSlang) {
            btnSlang.setTextColor(Color.parseColor("#1E35CB"))
            btnGrammar.setTextColor(Color.parseColor("#7D7E82"))

        // 디미 유진_신조어 탭 선택 상태
        } else {
            btnSlang.setTextColor(Color.parseColor("#555555"))
            btnGrammar.setTextColor(Color.parseColor("#1E35CB"))
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(mainLayout)

        if (isSlang) {

            constraintSet.connect(R.id.tabIndicator, ConstraintSet.START, R.id.btnSlang, ConstraintSet.START)
            constraintSet.connect(R.id.tabIndicator, ConstraintSet.END, R.id.btnSlang, ConstraintSet.END)
        } else {

            constraintSet.connect(R.id.tabIndicator, ConstraintSet.START, R.id.btnGrammar, ConstraintSet.START)
            constraintSet.connect(R.id.tabIndicator, ConstraintSet.END, R.id.btnGrammar, ConstraintSet.END)
        }
        constraintSet.applyTo(mainLayout)
    }

    // 디미 유진_오답 전체 삭제 확인 다이얼로그 표시
    private fun showClearDialog() {
        AlertDialog.Builder(this)
            .setTitle("오답 초기화")
            .setMessage("오답 내역을 모두 삭제할까요?")
            .setPositiveButton("삭제") { _, _ ->

                // 로컬(SQLite) 삭제
                WrongDBManager(this).clearAllWrong(this)

                // Firestore 삭제
                FirestoreWrongNote().clearAll(
                    type = currentType,
                    onSuccess = {
                        runOnUiThread {
                            Toast.makeText(this, "오답이 초기화되었습니다", Toast.LENGTH_SHORT).show()

                            // 🔥 recreate()도 되지만, ViewPager 어댑터 재세팅이 더 확실
                            val viewPager = findViewById<ViewPager2>(R.id.viewPager)
                            viewPager.adapter = WrongPagerAdapter(this)
                            viewPager.setCurrentItem(if (currentType == "slang") 0 else 1, false)
                        }
                    },
                    onFail = { e ->
                        runOnUiThread {
                            Toast.makeText(this, "Firestore 초기화 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
