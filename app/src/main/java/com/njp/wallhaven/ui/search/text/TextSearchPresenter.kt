package com.njp.wallhaven.ui.search.text

import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository
import com.njp.wallhaven.repositories.bean.History
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TextSearchPresenter(view: TextSearchContract.View) : BasePresenter<TextSearchContract.View>(view), TextSearchContract.Presenter {

    private var page = 1

    override fun refreshImages(q: String, ratios: String, colors: String, sorting: String, topRange: String, categories: String) {
        page = 1
        Repository.getInstance().searchByText(q, ratios, colors, sorting, topRange, categories, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { if (it.isEmpty()) view?.onNoImages() else view?.onRefreshImages(it) },
                        { view?.onRefreshImagesFail("网络连接失败 Q_Q") }
                )?.let { addDisposable(it) }
    }

    override fun loadMoreImages(q: String, ratios: String, colors: String, sorting: String, topRange: String, categories: String) {
        Repository.getInstance().searchByText(q, ratios, colors, sorting, topRange, categories, ++page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { if (it.isEmpty()) view?.onNoMoreImages() else view?.onLoadMoreImages(it) },
                        {
                            view?.onLoadMoreImagesFail("网络连接失败 Q_Q")
                            page--
                        }
                )?.let { addDisposable(it) }
    }

    override fun saveHistory(string: String) {
        Repository.getInstance().saveHistory(History(string, System.currentTimeMillis()))
    }

}