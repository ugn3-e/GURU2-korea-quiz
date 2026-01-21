package com.example.guru2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


//날짜별 오답 그룹을 보여주는 RecyclerView 어댑터
//세로 RecyclerView
//각 아이템 안에 가로 RecyclerView(해당 날짜의 오답 문제 목록) 포함

class WrongDateAdapter(
    // 디미 유진_날짜별 오답 그룹 리스트
    private val list: List<WrongDateGroup>,
    // 디미 유진_오답 문제 클릭 시 전달할 콜백 (quizId)
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<WrongDateAdapter.VH>() {

    // 디미 유진_날짜 하나(하루)를 표현하는 ViewHolder
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        // 디미 유진_해당 날짜의 오답 문제들을 가로로 보여주는 RecyclerView
        val recycler: RecyclerView = view.findViewById(R.id.recyclerHorizontal)
    }

    // 디미 유진_ViewHolder 생성
    // 디미 유진_item_wrong_date.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wrong_date, parent, false)
        return VH(view)
    }

    // 디미 유진_날짜별 데이터 바인딩 (group.data)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val group = list[position]

        // group.date 예: "2026-01-25"
        val parts = group.date.split("-")

        // 월(month)을 숫자 → 영문으로 변환
        val day = parts.getOrNull(2) ?: ""
        val month = when (parts.getOrNull(1)) {
            "01" -> "Jan"
            "02" -> "Feb"
            "03" -> "Mar"
            "04" -> "Apr"
            "05" -> "May"
            "06" -> "Jun"
            "07" -> "Jul"
            "08" -> "Aug"
            "09" -> "Sep"
            "10" -> "Oct"
            "11" -> "Nov"
            "12" -> "Dec"
            else -> ""
        }

        // 디미 유진_날짜 텍스트 적용
        holder.tvDay.text = day
        holder.tvMonth.text = month

        // 디미 유진_가로 RecyclerView 설정
        holder.recycler.layoutManager =
            LinearLayoutManager(
                holder.itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        // 디미 유진_오답 문제 리스트 어댑터 연결
        holder.recycler.adapter = WrongItemAdapter(group.items, onClick)
    }

    // 디미 유진_날짜 그룹 개수 반환
    override fun getItemCount(): Int = list.size
}
