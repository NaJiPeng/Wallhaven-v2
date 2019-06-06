package com.njp.wallhaven3.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import com.njp.wallhaven3.R
import java.lang.StringBuilder

class CategoriesAdapter(
        private val list: List<String>,
        private val color: Int,
        private val listener: (String) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private val checked = Array(list.size) { 1 }
    private var categories: String = "111"
        get() = StringBuilder().apply {
            checked.forEach {
                append("$it")
            }
        }.toString()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(p0.context)
                .inflate(R.layout.item_drop_down, p0, false)
        return CategoriesAdapter.ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textItem.text = list[position]
        if (checked[position] == 1) {
            viewHolder.drawable.setColor(color)
            viewHolder.textItem.setTextColor(Color.parseColor("#ffffff"))
        } else {
            viewHolder.drawable.setColor(Color.parseColor("#eeeeee"))
            viewHolder.textItem.setTextColor(Color.parseColor("#000000"))
        }
        viewHolder.itemView.setOnClickListener {
            select(position)
            listener.invoke(categories)
        }
    }

    private fun select(position: Int) {
        checked[position] = if (checked[position] == 0) 1 else 0
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textItem = itemView.findViewById<TextView>(R.id.textItem)!!
        val drawable = itemView.background as GradientDrawable
    }
}