package com.njp.wallhaven.ui.main

import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainPresenter(view: MainContract.View, val path: String) : MainContract.Presenter, BasePresenter<MainContract.View>(view) {

    private var page = 1

    override fun onRefreshImages() {
        Repository.getInstance().getImages(path, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    page = 1
                    if (images.isNotEmpty()) {
                        view?.onRefreshImages(images)
                    } else {
                        view?.onRefreshImagesFail("没有加载到图片 T_T")
                    }
                }, {
                    view?.onRefreshImagesFail("网络连接失败 Q_Q")
                })?.let { addDisposable(it) }
    }

    override fun onLoadMoreImages() {
        Repository.getInstance().getImages(path, ++page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    if (images.isNotEmpty()) {
                        view?.onLoadMoreImages(images)
                    } else {
                        view?.onNoMore()
                        page--
                    }
                }, {
                    view?.onLoadMoreImagesFail("网络连接失败 Q_Q")
                    page--
                }
                )?.let { addDisposable(it) }
    }

}