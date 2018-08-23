package com.njp.wallhaven.ui.history

import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository

class HistoryPresenter(view: HistoryContract.View) : HistoryContract.Presenter, BasePresenter<HistoryContract.View>(view) {

    private var page = 0

    override fun refreshImages() {
        page = 0
        val images = Repository.getInstance().getHistory(0)
        if (images.isNotEmpty()) view?.onRefreshImages(images) else view?.onNoImages()
    }

    override fun loadMoreImages() {
        val images = Repository.getInstance().getHistory(++page)
        if (images.isNotEmpty()) {
            view?.onLoadMoreImages(images)
        } else {
            view?.onNoMoreImages()
            page--
        }
    }

    override fun clearHistoryImages() {
        Repository.getInstance().clearHistory()
    }

}