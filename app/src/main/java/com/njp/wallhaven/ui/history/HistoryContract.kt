package com.njp.wallhaven.ui.history

interface HistoryContract {

    interface View {
        fun onHistoryImages(data: List<Any>)
        fun onNoHistoryImages()
    }

    interface Presenter {
        fun getHistoryImages()
        fun clearHistoryImages()
    }

}