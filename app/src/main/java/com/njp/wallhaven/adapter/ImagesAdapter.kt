package com.njp.wallhaven.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.njp.wallhaven.R
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.ui.detail.DetailActivity
import com.njp.wallhaven.utils.CommonDataHolder

class ImagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data: MutableList<Any> = ArrayList()

    fun setData(data: List<Any>) {
        val size = itemCount
        this.data.clear()
        notifyItemRangeRemoved(0, size)
        this.data.addAll(data)
        notifyItemRangeInserted(0, itemCount)
    }

    fun addData(data: List<Any>) {
        val start = itemCount
        this.data.addAll(data)
        notifyItemRangeInserted(start, data.size)
    }

    fun clear(){
        val size = itemCount
        this.data.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] is SimpleImageInfo) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image, parent, false)
            ImageViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date, parent, false)
            DateViewHolder(itemView)
        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            val image = data[position] as SimpleImageInfo
            Glide.with(viewHolder.itemView.context)
                    .load(image.url)
                    .apply(RequestOptions().centerCrop())
                    .into((viewHolder as ImageViewHolder).image)
            viewHolder.itemView.setOnClickListener { _ ->
                val images = data.filterIsInstance<SimpleImageInfo>()
                CommonDataHolder.setSimpleData(images)
                DetailActivity.actionStart(viewHolder.itemView.context, images.indexOf(image))
            }
        } else {
            val date = data[position] as String
            (viewHolder as DateViewHolder).textDate.text = date
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        (recyclerView.layoutManager as GridLayoutManager).spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (getItemViewType(position) == 0) 1 else 2
            }
        }
    }


    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image)!!
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate = itemView.findViewById<TextView>(R.id.textDate)!!
    }


}