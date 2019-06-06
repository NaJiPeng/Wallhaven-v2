package com.njp.wallhaven3.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.njp.wallhaven3.R

class ColorDropMenuAdpter(
        private val colors: List<String>,
        private val color: Int,
        private var selectIndex: Int = 0,
        private val listener: (Int) -> Unit
) : RecyclerView.Adapter<ColorDropMenuAdpter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(p0.context)
                .inflate(R.layout.item_drop_menu_color, p0, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return colors.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#${colors[position]}"))
        if (position == 0) {
            viewHolder.textUnlimited.text = "不限"
        }
        if (position == selectIndex) {
            viewHolder.drawable.setStroke(10, color)
        }else{
            viewHolder.drawable.setStroke(0, Color.parseColor("#00000000"))
        }
        viewHolder.itemView.setOnClickListener {
            select(position)
            listener.invoke(position)
        }
    }

    private fun select(position: Int) {
        selectIndex = position
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textUnlimited = itemView.findViewById<TextView>(R.id.textUnlimited)
        val cardView = itemView.findViewById<CardView>(R.id.cardColor)
        val drawable = itemView.background as GradientDrawable
    }
}