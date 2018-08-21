package com.njp.wallhaven.ui.stared

import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository

class StaredPresenter(view: StaredContract.View) : BasePresenter<StaredContract.View>(view), StaredContract.Presenter {
    override fun getStaredImages() {
        val images = Repository.getInstance().getStartedImages()
        if (images.isEmpty()) view?.onNoStaredImages() else view?.onStaredImages(images)
    }


}