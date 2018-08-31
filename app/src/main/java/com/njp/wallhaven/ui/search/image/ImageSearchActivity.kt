package com.njp.wallhaven.ui.search.image

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.njp.wallhaven.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_image_search.*


class ImageSearchActivity : AppCompatActivity() {

    companion object {
        fun actionStart(context: Context, path: String) {
            val intent = Intent(context, ImageSearchActivity::class.java)
            intent.putExtra("path", path)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_search)

        val path = intent.getStringExtra("path")
        Log.d("wwww",path)
        path?.let {
            Glide.with(this)
                    .load(it)
                    .apply(RequestOptions().apply {
                        skipMemoryCache(true)
                        diskCacheStrategy(DiskCacheStrategy.NONE)
                    })
                    .into(image)
        }
    }


}
