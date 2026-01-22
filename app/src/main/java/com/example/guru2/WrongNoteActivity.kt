package com.example.guru2

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.guru2.fire.FirestoreWrongNote

// 오답 노트 메인 화면 Activity
// 신조어 / 맞춤법 오답 탭 전환 및 초기화 기능 담당

class WrongNoteActivity : AppCompatActivity() {
    private var currentType: String = "slang"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrong_note)

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

    // 디미 유진_선택된 탭에 따라 텍스트 색상 변경
    private fun selectTab(isSlang: Boolean) {
        val btnSlang = findViewById<TextView>(R.id.btnSlang)
        val btnGrammar = findViewById<TextView>(R.id.btnGrammar)

        // 디미 유진_신조어 탭 선택 상태
        if (isSlang) {
            btnSlang.setTextColor(Color.parseColor("#6851A5"))
            btnGrammar.setTextColor(Color.parseColor("#555555"))

        // 디미 유진_신조어 탭 선택 상태
        } else {
            btnSlang.setTextColor(Color.parseColor("#555555"))
            btnGrammar.setTextColor(Color.parseColor("#6851A5"))
        }
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
