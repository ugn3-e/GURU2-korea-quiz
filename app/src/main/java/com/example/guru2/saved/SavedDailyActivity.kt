// Empyt view Activity를 만들어 .kt와 .xml 파일이 함께 만들어졌으나 .xml만 쓰게 됨
// 혹시 오류가 생길 일을 방지하여 해당 파일을 삭제하지 않고 두었음
package com.example.guru2.saved

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guru2.R

// 저장한 콘텐츠의 날짜 단위 화면 레이아웃용
class SavedDailyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_saved_daily)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}