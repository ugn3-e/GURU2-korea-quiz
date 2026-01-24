package com.example.guru2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 저장된 퀴즈들을 날짜별로 보여줌
// 중첩 리사이클러뷰 외부 어댑터
// 세로가 날짜, 가로가 퀴즈 리스트

class SavedDailyDBManager(private val sections: List<DailySection>) :
    RecyclerView.Adapter<SavedDailyDBManager.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 날짜 표시 텍스트 뷰
        val dateText: TextView = view.findViewById(R.id.saved_date)
        // 날짜별로 카드를 가로로 보여줌 -> 내부 리사이클러뷰
        val horizontalRv: RecyclerView = view.findViewById(R.id.saved_cards)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_saved_daily, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position] // 현재 순서의 날짜 섹션 데이터 가져오기
        holder.dateText.text = section.date

        // 중첩 리사이클러뷰 설정 (가로 목록)
        holder.horizontalRv.apply {
            // 가로로 배치
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            // 내부 어댑터에 현재 날짜의 퀴즈 리스트 전달
            adapter = SavedQuizDBManager(section.quizzes)
            setHasFixedSize(true) // 크기 일정
        }
    }

    override fun getItemCount(): Int = sections.size
}