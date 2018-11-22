package com.njp.wallhaven.ui.splash.gallerys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.njp.wallhaven.R
import com.njp.wallhaven.repositories.bean.SimpleImageInfo

/**
 * 拼图视图
 */
class GalleryFragment4 : GalleryFragment() {

    private val imageViews = ArrayList<ImageView>(7)
    private lateinit var images: List<SimpleImageInfo>

    override fun setImages(images: List<SimpleImageInfo>) {
        this.images = images
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_4, container, false)
        imageViews.add(view.findViewById(R.id.imageView1))
        imageViews.add(view.findViewById(R.id.imageView2))
        imageViews.add(view.findViewById(R.id.imageView3))
        imageViews.add(view.findViewById(R.id.imageView4))
        imageViews.add(view.findViewById(R.id.imageView5))
        imageViews.add(view.findViewById(R.id.imageView6))
        imageViews.add(view.findViewById(R.id.imageView7))

        imageViews.forEachIndexed { i: Int, imageView: ImageView ->
            Glide.with(this)
                    .load(images[i].url)
                    .apply(RequestOptions().apply {
                        centerCrop()
                        placeholder(R.drawable.splash_backgroung)
                    })
                    .into(imageView)
        }
        return view
    }

}