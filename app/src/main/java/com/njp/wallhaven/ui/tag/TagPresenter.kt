package com.njp.wallhaven.ui.tag

import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TagPresenter(view: TagContract.View):BasePresenter<TagContract.View>(view),TagContract.Presenter {


    override fun getTagImageInfo(tagId: Int) {
        Repository.getInstance().getTagImageInfo(tagId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { view?.onGetTagImageInfo(it) },
                        { view?.onGetTagImageInfoFail("网络连接失败 Q_Q") }
                ).let { addDisposable(it) }
    }

}