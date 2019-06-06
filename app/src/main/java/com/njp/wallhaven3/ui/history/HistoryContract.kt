package com.njp.wallhaven3.ui.history

import com.njp.wallhaven3.repositories.bean.SimpleImageInfo

interface HistoryContract {

    interface View {
        fun onRefreshImages(data: List<SimpleImageInfo>)
        fun onLoadMoreImages(data: List<SimpleImageInfo>)
        fun onNoImages()
        fun onNoMoreImages()
    }

    interface Presenter {
        fun refreshImages()
        fun loadMoreImages()
        fun clearHistoryImages()
    }

}