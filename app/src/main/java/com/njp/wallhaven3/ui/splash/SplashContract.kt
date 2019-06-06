package com.njp.wallhaven3.ui.splash

import com.njp.wallhaven3.repositories.bean.SimpleImageInfo

interface SplashContract {

    interface View {
        //获取闪屏图
        fun onSplashImages(image: SimpleImageInfo)
        //没有闪屏图
        fun onNoSplashImage()
        //计时器
        fun onTimer()

    }

    interface Presenter {
        //抽取闪屏图
        fun getSplashImages()
        //更新本地图库
        fun updateSplashImages()
        //启动定时器
        fun startTimer()
    }

}