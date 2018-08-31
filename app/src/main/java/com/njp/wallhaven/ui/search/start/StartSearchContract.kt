package com.njp.wallhaven.ui.search.start

import com.njp.wallhaven.repositories.bean.History
import com.njp.wallhaven.repositories.bean.Tag

interface StartSearchContract {

    interface View {
        fun onGetStaredTags(tags: List<Tag>)
        fun onGetSearchHistory(historyList: List<History>)
    }

    interface Presenter {
        fun saveHistory(string: String)
        fun getStaredTags()
        fun getSearchHistory()
        fun deleteTag(tag: Tag)
        fun deleteHistory(history: History)
        fun clearHistory()
    }

}