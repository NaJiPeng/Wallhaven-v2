package com.njp.wallhaven.ui.main

import android.util.Log
import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainPresenter(view: MainContract.View) : MainContract.Presenter, BasePresenter<MainContract.View>(view) {

    override fun onRefreshImages(path: String) {
        Repository.getInstance().getImages(path, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    if (images.isNotEmpty()) {
                        view?.onRefreshImages(images)
                    } else {
                        view?.onRefreshImagesFail("没有加载到图片 T_T")
                    }
                }, {
                    view?.onRefreshImagesFail("网络连接失败 Q_Q")
                })?.let { addDisposable(it) }
    }

    override fun onLoadMoreImages(path: String, page: Int) {
        Repository.getInstance().getImages(path, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ images ->
                    if (images.isNotEmpty()) {
                        view?.onLoadMoreImages(images)
                    } else {
                        view?.onNoMore()
                    }
                }, {
                    view?.onLoadMoreImagesFail("网络连接失败 Q_Q")
                }
                )?.let { addDisposable(it) }
    }

}