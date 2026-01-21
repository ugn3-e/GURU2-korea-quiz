package com.example.guru2

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class WrongNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrong_note)

        val btnSlang = findViewById<TextView>(R.id.btnSlang)
        val btnGrammar = findViewById<TextView>(R.id.btnGrammar)
        val btnClearWrong = findViewById<Button>(R.id.btnClearWrong)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        viewPager.adapter = WrongPagerAdapter(this)

        // ⭐ 핵심: 스와이프 금지
        viewPager.isUserInputEnabled = false

        btnSlang.setOnClickListener {
            viewPager.setCurrentItem(0, false)
            selectTab(true)
        }

        btnGrammar.setOnClickListener {
            viewPager.setCurrentItem(1, false)
            selectTab(false)
        }

        btnClearWrong.setOnClickListener { showClearDialog() }

        selectTab(true)
    }


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

    private fun showClearDialog() {
        AlertDialog.Builder(this)
            .setTitle("오답 초기화")
            .setMessage("오답 내역을 모두 삭제할까요?")
            .setPositiveButton("삭제") { _, _ ->
                WrongDBManager(this).clearAllWrong(this)
                Toast.makeText(this, "오답이 초기화되었습니다", Toast.LENGTH_SHORT).show()
                recreate()
            }
            .setNegativeButton("취소", null)
            .show()
    }
}
