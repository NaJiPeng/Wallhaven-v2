package com.njp.wallhaven3.base

import android.app.Application
import com.njp.wallhaven3.R
import com.njp.wallhaven3.utils.*
import com.raizlabs.android.dbflow.config.FlowManager
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout


class MyApplication : Application() {

    companion object {
        lateinit var application: MyApplication
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        //ToastUtil初始化
        ToastUtil.init(this)
        //SPUtil初始化
        SPUtil.init(this)
        ColorUtil.init(this)
        //刷新框架初始化
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.blue, android.R.color.white)
            MaterialHeader(context)
        }
        //DBFlow初始化
        FlowManager.init(this)

        TencentUtil.init(this)

        UriUtil.init(this)

    }

}