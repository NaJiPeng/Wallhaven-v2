package com.njp.wallhaven.ui.stared

import com.njp.wallhaven.repositories.bean.SimpleImageInfo

interface StaredContract {

    interface View {
        fun onStaredImages(images: List<SimpleImageInfo>)
        fun onNoStaredImages()
    }

    interface Presenter {
        fun getStaredImages()
    }

}