package com.njp.wallhaven3.ui.tag

import com.njp.wallhaven3.base.BasePresenter
import com.njp.wallhaven3.repositories.Repository
import com.njp.wallhaven3.repositories.bean.Tag
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TagPresenter(view: TagContract.View) : BasePresenter<TagContract.View>(view), TagContract.Presenter {

    override fun getTagImageInfo(tagId: String) {
        Repository.getInstance().getTagImageInfo(tagId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { view?.onGetTagImageInfo(it) },
                        { view?.onGetTagImageInfoFail("网络连接失败 Q_Q") }
                ).let { addDisposable(it) }
    }

    override fun starTag(tag: Tag) {
        Repository.getInstance().starTag(tag)
    }

    override fun unStarTag(tag: Tag) {
        Repository.getInstance().unStarTag(tag)
    }

    override fun isTagStared(tag: Tag): Boolean {
        return Repository.getInstance().isTagStared(tag)
    }

}