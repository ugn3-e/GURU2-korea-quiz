package com.example.guru2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 레이아웃 설정(합치기)
        setContentView(R.layout.activity_saved_content)

        // DB 가져오기
        val dbManager = DBManager(this)

        val allQuizzes = dbManager.getSavedQuizzes()

        // 저장되었는지 로그 확인
        android.util.Log.d("DB_CHECK", "가져온 저장 데이터 개수: ${allQuizzes.size}")

        if (allQuizzes.isNotEmpty()) {
            // DB saved_date 필드 기준으로 그룹화
            val grouped = allQuizzes.groupBy { it.saved_date ?: "Unknown" }

            // 그룹화된 데이터를 DailySection 리스트로 변환시킴
            val displayList = grouped.map { (date, quizzes) ->
                DailySection(date, quizzes)
            }.reversed()

            // 어댑터에 전달
            val mainRv = findViewById<RecyclerView>(R.id.rv_main_vertical)
            mainRv.layoutManager = LinearLayoutManager(this)
            mainRv.adapter = SavedDailyDBManager(displayList)
        } else {
            android.util.Log.d("DB_CHECK", "저장된 데이터가 하나도 없습니다.")
        }
    }
}