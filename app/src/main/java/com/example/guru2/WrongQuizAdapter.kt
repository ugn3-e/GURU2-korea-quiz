package com.example.guru2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 가로 스크롤 RecyclerView에서 오답 단어 목록을 표시하는 어댑터

class WrongQuizAdapter(

    // 디미 유진_가로로 나열될 오답 문제 리스트
    private val list: List<WrongItem>,

    // 디미 유진_오답 단어 클릭 시 호출되는 콜백
    // -> 선택된 WrongItem 전체를 상위로 전달
    private val onClick: (WrongItem) -> Unit
) : RecyclerView.Adapter<WrongQuizAdapter.VH>() {

    // 디미 유진_오답 단어 하나를 표현하는 ViewHolder
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {

        // 디미 유진_화면에 표시될 오답 단어 텍스트
        val tvWord: TextView = view.findViewById(R.id.tvWord)
    }

    // 디미 유진_ViewHolder 생성 (item_wrong_word.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wrong_word, parent, false)
        return VH(view)
    }

    // 디미 유진_오답 단어 데이터 바인딩
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        // 디미 유진_오답 단어 텍스트 설정
        holder.tvWord.text = item.displayText

        // 디미 유진_오답 단어 클릭 시 상위에서 정의한 동작 실행
        holder.itemView.setOnClickListener { onClick(item) }
    }

    // 디미 유진_오답 단어 개수 반환
    override fun getItemCount(): Int = list.size
}
