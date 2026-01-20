package com.example.guru2.ui.wrong

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.guru2.R
import com.example.guru2.WrongDBManager

// 디미 유진_오답 노트
class WrongNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrong_note)

        // 디미 유진_View 참조
        val btnSlang = findViewById<TextView>(R.id.btnSlang)
        val btnGrammar = findViewById<TextView>(R.id.btnGrammar)
        val btnClearWrong = findViewById<Button>(R.id.btnClearWrong)

        // 최초 화면: 신조어 오답
        showFragment(WrongListFragment.newSlangInstance())
        selectTab(isSlang = true)

        // 신조어 탭
        btnSlang.setOnClickListener {
            showFragment(WrongListFragment.newSlangInstance())
            selectTab(isSlang = true)
        }

        // 맞춤법 탭
        btnGrammar.setOnClickListener {
            showFragment(WrongListFragment.newSpellingInstance())
            selectTab(isSlang = false)
        }

        // 오답 초기화
        btnClearWrong.setOnClickListener {
            showClearDialog()
        }
    }

    // Fragment 교체
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentContainer, fragment)
            .commit()
    }

    // 서브바 선택 상태 표시
    private fun selectTab(isSlang: Boolean) {
        val btnSlang = findViewById<TextView>(R.id.btnSlang)
        val btnGrammar = findViewById<TextView>(R.id.btnGrammar)

        if (isSlang) {
            btnSlang.setTextColor(Color.parseColor("#6851A5"))
            btnGrammar.setTextColor(Color.parseColor("#555555"))
        } else {
            btnSlang.setTextColor(Color.parseColor("#555555"))
            btnGrammar.setTextColor(Color.parseColor("#6851A5"))
        }
    }

    // 오답 초기화 다이얼로그
    private fun showClearDialog() {
        AlertDialog.Builder(this)
            .setTitle("오답 초기화")
            .setMessage("오답 내역을 모두 삭제할까요?\n(문제 원본은 삭제되지 않습니다)")
            .setPositiveButton("삭제") { _, _ ->
                WrongDBManager(this).clearAllWrong(this)
                Toast.makeText(this, "오답이 초기화되었습니다", Toast.LENGTH_SHORT).show()
                recreate()
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
