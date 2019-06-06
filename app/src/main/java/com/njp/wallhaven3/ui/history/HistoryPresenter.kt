package com.njp.wallhaven3.ui.history

import com.njp.wallhaven3.base.BasePresenter
import com.njp.wallhaven3.repositories.Repository

class HistoryPresenter(view: HistoryContract.View) : HistoryContract.Presenter, BasePresenter<HistoryContract.View>(view) {

    private var page = 0

    override fun refreshImages() {
        page = 0
        val images = Repository.getInstance().getHistoryImages(0)
        if (images.isNotEmpty()) view?.onRefreshImages(images) else view?.onNoImages()
    }

    override fun loadMoreImages() {
        val images = Repository.getInstance().getHistoryImages(++page)
        if (images.isNotEmpty()) {
            view?.onLoadMoreImages(images)
        } else {
            view?.onNoMoreImages()
            page--
        }
    }

    override fun clearHistoryImages() {
        Repository.getInstance().clearHistoryImages()
    }

}