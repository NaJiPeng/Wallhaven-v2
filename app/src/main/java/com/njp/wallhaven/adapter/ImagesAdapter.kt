package com.njp.wallhaven.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.njp.wallhaven.R
import com.njp.wallhaven.repositories.bean.SimpleImageInfo

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    private val images: MutableList<SimpleImageInfo> = ArrayList()

    fun setImages(images: List<SimpleImageInfo>) {
        val size = itemCount
        this.images.clear()
        notifyItemRangeRemoved(0, size)
        this.images.addAll(images)
        notifyItemRangeInserted(0, itemCount)
    }

    fun addImages(images: List<SimpleImageInfo>) {
        val start = itemCount
        this.images.addAll(images)
        notifyItemRangeInserted(start, images.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(viewHolder.itemView.context)
                .load(images[position].url)
                .apply(RequestOptions().centerCrop())
                .into(viewHolder.image)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image)
    }
}