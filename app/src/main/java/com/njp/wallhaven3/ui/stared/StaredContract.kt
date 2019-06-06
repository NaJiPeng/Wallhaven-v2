package com.njp.wallhaven3.ui.stared

import com.njp.wallhaven3.repositories.bean.SimpleImageInfo

interface StaredContract {

    interface View {
        fun onRefreshImages(images: List<SimpleImageInfo>)
        fun onLoadMoreImages(images: List<SimpleImageInfo>)
        fun onNoMoreImages()
        fun onNoImages()
    }

    interface Presenter {
        fun refreshImages()
        fun loadMoreImages()
    }

}