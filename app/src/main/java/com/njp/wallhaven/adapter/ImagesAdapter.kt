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
import com.njp.wallhaven.ui.detail.DetailActivity

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    private val images: MutableList<SimpleImageInfo> = ArrayList()

    fun setData(data: List<SimpleImageInfo>) {
        val size = itemCount
        this.images.clear()
        notifyItemRangeRemoved(0, size)
        this.images.addAll(data)
        notifyItemRangeInserted(0, itemCount)
    }

    fun addData(data: List<SimpleImageInfo>) {
        val start = itemCount
        this.images.addAll(data)
        notifyItemRangeInserted(start, data.size)
    }

    fun clear() {
        val size = itemCount
        this.images.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
        return ViewHolder(itemView)

    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val image = images[position]
        Glide.with(viewHolder.itemView.context)
                .load(image.url)
                .apply(RequestOptions().centerCrop())
                .into(viewHolder.image)
        viewHolder.itemView.setOnClickListener { _ ->
            DetailActivity.actionStart(viewHolder.itemView.context, images, position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image)!!
    }


}