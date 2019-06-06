package com.njp.wallhaven3.adapter

import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.njp.wallhaven3.R

class BottomSheetAdapter(private val list: List<Pair<String, Int>>, private val listener: (Pair<String, Int>) -> Unit) : RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.item_bottomsheet, p0, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pair = list[position]
        holder.imageColor.setImageDrawable(ColorDrawable(pair.second))
        holder.textColor.text = pair.first
        holder.textColor.setTextColor(pair.second)
        holder.itemView.setOnClickListener { listener.invoke(pair) }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageColor = itemView.findViewById<ImageView>(R.id.imageColor)
        val textColor = itemView.findViewById<TextView>(R.id.textColor)
    }
}