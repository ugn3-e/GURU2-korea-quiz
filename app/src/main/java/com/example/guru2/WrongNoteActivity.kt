package com.example.guru2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
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
        }

        // 상단 탭 버튼
        val btnSlang = findViewById<TextView>(R.id.btnSlang)
        val btnGrammar = findViewById<TextView>(R.id.btnGrammar)

        // 홈으로 돌아가기 버튼
        val btnGoHome = findViewById<Button>(R.id.btnGoHome)

        // 오답 목록을 보여줄 ViewPager
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // ViewPager 어댑터 설정 (신조어 / 맞춤법 Fragment)
        viewPager.adapter = WrongPagerAdapter(this)

        // 사용자 스와이프로 페이지 이동 금지 -> 탭 버튼 클릭으로만 전환
        viewPager.isUserInputEnabled = false

        // 신조어 탭 클릭
        btnSlang.setOnClickListener {
            currentType = "slang"
            viewPager.setCurrentItem(0, false)
            selectTab(true)
        }

        // 맞춤법 탭 클릭
        btnGrammar.setOnClickListener {
            currentType = "spell"
            viewPager.setCurrentItem(1, false)
            selectTab(false)
        }

        // 홈으로 돌아가기
        btnGoHome.setOnClickListener {
            finish()
        }

        // 처음 접속하면 신조어 탭이 선택되도록 설정
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

            // 마이페이지
            R.id.action_mypage -> {
                startActivity(Intent(this, com.example.guru2.mypage.MyPageActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 선택된 탭에 따라 텍스트 색상 변경
    private fun selectTab(isSlang: Boolean) {
        val btnSlang = findViewById<TextView>(R.id.btnSlang)
        val btnGrammar = findViewById<TextView>(R.id.btnGrammar)
        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.mainLayout)


        // 신조어 탭 선택 상태
        if (isSlang) {
            btnSlang.setTextColor(Color.parseColor("#1E35CB"))
            btnGrammar.setTextColor(Color.parseColor("#7D7E82"))

        // 신조어 탭 선택 상태
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
}
