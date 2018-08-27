package com.njp.wallhaven.ui.search.start

import android.nfc.Tag
import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.bean.History

class StartSearchPresenter(view: StartSearchContract.View):BasePresenter<StartSearchContract.View>(view),StartSearchContract.Presenter {

    override fun getStaredTags() {

    }

    override fun getSearchHistory() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTag(tag: Tag) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteHistory(history: History) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearHistory() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}