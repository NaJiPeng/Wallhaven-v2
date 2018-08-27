package com.njp.wallhaven.ui.search.start

import android.nfc.Tag
import com.njp.wallhaven.repositories.bean.History

interface StartSearchContract {

    interface View {
        fun onGetStaredTags(tags: List<Tag>)
        fun onGetSearchhistory(history: List<History>)
    }

    interface Presenter {
        fun getStaredTags()
        fun getSearchHistory()
        fun deleteTag(tag: Tag)
        fun deleteHistory(history: History)
        fun clearHistory()
    }

}