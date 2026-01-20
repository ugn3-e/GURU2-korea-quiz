package com.example.guru2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedDailyDBManager(private val sections: List<DailySection>) :
    RecyclerView.Adapter<SavedDailyDBManager.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateText: TextView = view.findViewById(R.id.saved_date)
        val horizontalRv: RecyclerView = view.findViewById(R.id.saved_cards)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_saved_daily, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sections[position]
        holder.dateText.text = section.date

        // 중첩 리사이클러뷰 설정
        holder.horizontalRv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = SavedQuizDBManager(section.quizzes)
            setHasFixedSize(true)
        }
    }

    override fun getItemCount(): Int = sections.size
}