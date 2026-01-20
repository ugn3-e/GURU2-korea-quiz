package com.example.guru2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class SavedQuizDBManager(private val quizList: List<QuizData>) :
    RecyclerView.Adapter<SavedQuizDBManager.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.content_img)
        val title: TextView = view.findViewById(R.id.content_title)
        val ref: TextView = view.findViewById(R.id.content_ref)
        val root: View = view // 클릭을 위해 전체 레이아웃 추가함
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_saved_quiz, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = quizList[position]
        holder.title.text = item.source ?: "출처 없음"
        holder.ref.text = item.sentence

        holder.img.setImageResource(R.drawable.ic_launcher_foreground) // Db에 이미지 없어서 일단 기본 이미지

        holder.root.setOnClickListener {
            val context = holder.itemView.context

            // 클릭하면 해당 퀴즈의 상세 페이지로 이동
            val intent = Intent(context, SavedDetail::class.java)

            // 클릭한 퀴즈의 ID를 전달
            intent.putExtra("quiz_id", item.id)
            intent.putExtra("is_saved_view", true)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = quizList.size

}
