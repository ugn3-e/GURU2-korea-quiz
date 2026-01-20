package com.example.guru2.ui.wrong

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guru2.R
import com.example.guru2.WrongDBManager

// 디미 유진_오답 퀴즈 목록 RecyclerView 어댑터
class WrongQuizAdapter(
    private val list: List<WrongDBManager.WrongQuiz>,
    private val onClick: (WrongDBManager.WrongQuiz) -> Unit
): RecyclerView.Adapter<WrongQuizAdapter.VH>() {

    // 디미 유진_ViewHolder (하나의 오답 문제 아이템 뷰, 문제 문장을 표시하는 TextView)
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuestion: TextView = view.findViewById(R.id.tvQuestion)
    }

    // 디미 유진_ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wrong_quiz, parent, false)
        return VH(view)
    }

    // 디미 유진_해당하는 오답 문제 데이터를 가져오기
    override fun onBindViewHolder(holder: VH, position: Int) {
        val quiz = list[position]
        holder.tvQuestion.text = quiz.question
        holder.itemView.setOnClickListener { onClick(quiz) }
    }

    // 디미 유진_RecyclerView에 표시할 오답 문제의 개수
    override fun getItemCount(): Int = list.size
}
