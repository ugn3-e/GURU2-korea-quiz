package com.example.guru2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WrongDateAdapter(
    private val list: List<WrongDateGroup>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<WrongDateAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        val recycler: RecyclerView = view.findViewById(R.id.recyclerHorizontal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wrong_date, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val group = list[position]

        // group.date 예: "2026-01-25"
        val parts = group.date.split("-")

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

        holder.tvDay.text = day
        holder.tvMonth.text = month

        holder.recycler.layoutManager =
            LinearLayoutManager(
                holder.itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        holder.recycler.adapter = WrongItemAdapter(group.items, onClick)
    }

    override fun getItemCount(): Int = list.size
}
