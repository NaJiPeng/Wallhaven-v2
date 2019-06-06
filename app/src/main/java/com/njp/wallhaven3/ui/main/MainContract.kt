package com.njp.wallhaven3.ui.main

import com.njp.wallhaven3.repositories.bean.SimpleImageInfo

interface MainContract {

    interface View {
        fun onRefreshImages(images: List<SimpleImageInfo>)
        fun onRefreshImagesFail(msg: String)
        fun onLoadMoreImages(images: List<SimpleImageInfo>)
        fun onLoadMoreImagesFail(msg: String)
        fun onNoMore()
    }

    interface Presenter {
        fun onRefreshImages()
        fun onLoadMoreImages()
    }

}