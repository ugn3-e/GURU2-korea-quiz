package com.example.guru2.saved

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.guru2.R

class SavedQuizDBManager(private val quizList: List<QuizData>) :
    RecyclerView.Adapter<SavedQuizDBManager.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.content_img)
        val title: TextView = view.findViewById(R.id.content_title)
        val ref: TextView = view.findViewById(R.id.content_ref)
        val root: View = view // 클릭을 위한 전체 레이아웃
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 저장한 콘텐츠 레이아웃 inflate
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_saved_quiz, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 현재 위치의 퀴즈 데이터
        val item = quizList[position]

        holder.title.text = item.source ?: "출처 없음"
        holder.ref.text = item.sentence

        // 이미지가 있을 경우 로드
        if (!item.image_path.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load("file:///android_asset/images/${item.image_path}")
                .error(R.drawable.ic_launcher_foreground) // 이미지 로딩 실패 시 기본 이미지
                .into(holder.img)
        } else {
            // 이미지가 없을 경우 기본 이미지 설정
            holder.img.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // 콘텐츠 클릭 시 상세 화면으로 이동
        holder.root.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, SavedDetail::class.java)

            // 클릭한 퀴즈의 ID를 전달
            intent.putExtra("quiz_id", item.id)
            intent.putExtra("is_saved_view", true)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = quizList.size

}