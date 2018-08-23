package com.njp.wallhaven.ui.stared

import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository

class StaredPresenter(view: StaredContract.View) : BasePresenter<StaredContract.View>(view), StaredContract.Presenter {

    private var page = 0

    override fun refreshImages() {
        page = 0
        val images = Repository.getInstance().getStartedImages(0)
        if (images.isNotEmpty()) view?.onRefreshImages(images) else view?.onNoImages()
    }

    override fun loadMoreImages() {
        val images = Repository.getInstance().getStartedImages(++page)
        if (images.isNotEmpty()) {
            view?.onLoadMoreImages(images)
        } else {
            view?.onNoMoreImages()
            page--
        }
    }

}