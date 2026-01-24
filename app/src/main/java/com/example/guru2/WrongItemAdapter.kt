package com.example.guru2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 오답 단어(문제) 목록용 RecyclerView 어댑터
// 날짜별 오답 그룹 안에서 사용됨
// 가로 RecyclerView에 표시되는 단일 오답 아이템 담당

class WrongItemAdapter(

    // 해당 날짜에 포함된 오답 문제 리스트
    private val list: List<WrongItem>,

    // 오답 클릭 시 quizId 전달 콜백
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<WrongItemAdapter.VH>() {

    // 오답 단어 하나를 표현하는 ViewHolder
    // tvWord : 신조어 단어 or 맞춤법 정답 텍스트
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvWord: TextView = view.findViewById(R.id.tvWord)
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wrong_word, parent, false)
        return VH(view)
    }

    // 오답 단어 데이터 바인딩
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        // 화면에 보여줄 텍스트 설정
        holder.tvWord.text = item.displayText

        // 오답 단어 클릭 시
        // -> 해당 문제의 quizId를 상위 화면으로 전달 -> 상세 오답 화면으로 이동
        holder.itemView.setOnClickListener { onClick(item.quizId) }
    }

    // 오답 아이템 개수 반환
    override fun getItemCount(): Int = list.size
}
