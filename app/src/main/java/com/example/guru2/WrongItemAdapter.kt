package com.example.guru2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WrongItemAdapter(
    private val list: List<WrongItem>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<WrongItemAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvWord: TextView = view.findViewById(R.id.tvWord)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wrong_word, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.tvWord.text = item.displayText
        holder.itemView.setOnClickListener { onClick(item.quizId) }
    }

    override fun getItemCount(): Int = list.size
}
