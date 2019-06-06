package com.njp.wallhaven3.ui.search.text

import com.njp.wallhaven3.repositories.bean.SimpleImageInfo

interface TextSearchContract {

    interface View {
        fun onRefreshImages(images: List<SimpleImageInfo>)
        fun onRefreshImagesFail(msg: String)
        fun onNoImages()
        fun onLoadMoreImages(images: List<SimpleImageInfo>)
        fun onLoadMoreImagesFail(msg: String)
        fun onNoMoreImages()
    }

    interface Presenter {
        fun refreshImages(q: String, ratios: String, colors: String, sorting: String, topRange: String, categories: String)
        fun loadMoreImages(q: String, ratios: String, colors: String, sorting: String, topRange: String, categories: String)
        fun saveHistory(string: String)
    }

}