package com.njp.wallhaven.base

import android.app.Application
import android.content.Context
import com.njp.wallhaven.R
import com.njp.wallhaven.utils.SPUtil
import com.njp.wallhaven.utils.ToastUtil
import com.scwang.smartrefresh.header.MaterialHeader
import skin.support.app.SkinCardViewInflater
import skin.support.constraint.app.SkinConstraintViewInflater
import skin.support.design.app.SkinMaterialViewInflater
import skin.support.SkinCompatManager
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator


class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        //换肤框架初始化
        SkinCompatManager.withoutActivity(this)
                .addInflater(SkinMaterialViewInflater())
                .addInflater(SkinConstraintViewInflater())
                .addInflater(SkinCardViewInflater())
                .setSkinStatusBarColorEnable(true)
                .setSkinWindowBackgroundEnable(true)
                .loadSkin()
        //ToastUtil初始化
        ToastUtil.init(this)
        //SPUtil初始化
        SPUtil.init(this)

        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white)
            MaterialHeader(context)
        }

    }

}