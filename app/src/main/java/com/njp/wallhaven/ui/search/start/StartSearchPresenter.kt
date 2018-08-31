package com.njp.wallhaven.ui.search.start

import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository
import com.njp.wallhaven.repositories.bean.History
import com.njp.wallhaven.repositories.bean.Tag

class StartSearchPresenter(view: StartSearchContract.View) : BasePresenter<StartSearchContract.View>(view), StartSearchContract.Presenter {

    override fun saveHistory(string: String) {
        Repository.getInstance().saveHistory(History(string, System.currentTimeMillis()))
    }

    override fun getStaredTags() {
        val tags = Repository.getInstance().getTags()
        view?.onGetStaredTags(tags)
    }

    override fun getSearchHistory() {
        val historyList = Repository.getInstance().getSearchHistory()
        view?.onGetSearchHistory(historyList)
    }

    override fun deleteTag(tag: Tag) {
        Repository.getInstance().unStarTag(tag)
    }

    override fun deleteHistory(history: History) {
        Repository.getInstance().deleteHistory(history)
    }

    override fun clearHistory() {
        Repository.getInstance().clearHistory()
    }


}