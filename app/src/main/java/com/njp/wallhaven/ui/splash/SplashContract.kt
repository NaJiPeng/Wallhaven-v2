package com.njp.wallhaven.ui.splash

import com.njp.wallhaven.bean.SimpleImageInfo

interface SplashContract {

    interface View {
        //获取一张闪屏图
        fun onSplashImage(imageInfo: SimpleImageInfo?)
        //计时器
        fun onTimer()

    }

    interface Presenter {
        //抽取闪屏图
        fun getSplashImage()
        //更新本地图库
        fun updateSplashImages()
        //启动定时器
        fun startTimer()
    }

}