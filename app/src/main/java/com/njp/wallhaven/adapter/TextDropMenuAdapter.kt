package com.njp.wallhaven.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.njp.wallhaven.R

class TextDropMenuAdapter(
        private val list: List<String>,
        private val color: Int,
        private var selectIndex: Int = 0,
        private val listener: (Int) -> Unit
) : RecyclerView.Adapter<TextDropMenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(p0.context)
                .inflate(R.layout.item_drop_down, p0, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textItem.text = list[position]
        if (position == selectIndex) {
            viewHolder.drawable.setColor(color)
            viewHolder.textItem.setTextColor(Color.parseColor("#ffffff"))
        } else {
            viewHolder.drawable.setColor(Color.parseColor("#eeeeee"))
            viewHolder.textItem.setTextColor(Color.parseColor("#000000"))
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
        val textItem = itemView.findViewById<TextView>(R.id.textItem)!!
        val drawable = itemView.background as GradientDrawable
    }
}