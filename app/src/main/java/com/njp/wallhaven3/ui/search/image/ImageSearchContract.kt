package com.njp.wallhaven3.ui.search.image

import android.content.Context
import com.njp.wallhaven3.repositories.bean.SimpleImageInfo

interface ImageSearchContract {

    interface View {
        fun onGetImages(images: List<SimpleImageInfo>)
        fun onGetImagesFail(msg: String)
        fun onGetNoImages()
    }

    interface Presenter {
        fun getImages(context: Context, path: String)
    }

}