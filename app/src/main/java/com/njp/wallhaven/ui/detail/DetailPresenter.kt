package com.njp.wallhaven.ui.detail

import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DetailPresenter(view: DetailContract.View) : BasePresenter<DetailContract.View>(view), DetailContract.Presenter {

    override fun getDetailImage(id: Int) {
        Repository.getInstance().getDetailImage(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { view?.onGetDetailImageSuccess(it) },
                        { view?.onGetDetailImageFail("网络连接失败 Q_Q") }
                )?.let { addDisposable(it) }
    }

    override fun starImage(image: SimpleImageInfo) {
        Repository.getInstance().starImage(image)
    }

    override fun unStarImage(image: SimpleImageInfo) {
        Repository.getInstance().unStarImage(image)
    }

    override fun isStared(image: SimpleImageInfo): Boolean {
        return Repository.getInstance().isStared(image)
    }


}