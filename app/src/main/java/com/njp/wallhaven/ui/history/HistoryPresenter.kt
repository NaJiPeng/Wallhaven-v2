package com.njp.wallhaven.ui.history

import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository

class HistoryPresenter(view: HistoryContract.View) : HistoryContract.Presenter, BasePresenter<HistoryContract.View>(view) {
    override fun getHistoryImages() {
        val images = Repository.getInstance().getHistory()
        if (images.isNotEmpty()) {
            val data = ArrayList<Any>()
            images.forEach {
                if (it.images?.isNotEmpty() == true) {
                    data.add(it.date)
                    data.addAll(it.images!!.asReversed())
                }
            }
            if (data.isEmpty()) view?.onNoHistoryImages() else view?.onHistoryImages(data)
        } else {
            view?.onNoHistoryImages()
        }
    }

    override fun clearHistoryImages() {
        Repository.getInstance().clearHistory()
    }

}