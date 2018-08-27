package com.njp.wallhaven.ui.search.start

import android.nfc.Tag
import android.os.Bundle
import com.njp.wallhaven.R
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.History

class StartSearchActivity : BaseActivity<StartSearchContract.View, StartSearchPresenter>(),StartSearchContract.View {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_search)
    }

    override fun onGetStaredTags(tags: List<Tag>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetSearchhistory(history: List<History>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
