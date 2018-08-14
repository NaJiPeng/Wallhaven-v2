package com.njp.wallhaven.repositories.local

import com.google.gson.Gson
import com.njp.wallhaven.bean.SplashImages
import com.njp.wallhaven.utils.SPUtil

/**
 * 本地数据存取接口
 */
class LocalDao {

    private val localDataSP = SPUtil.getDataSP()
    private val gson = Gson()

    fun saveSplashImages(data: SplashImages) {
        localDataSP.putString("splash_images", gson.toJson(data))
    }

    fun getSplashImages(): SplashImages? {
        val string = localDataSP.getString("splash_images", "")
        return if (string.isNotEmpty()) gson.fromJson(string, SplashImages::class.java) else null
    }

}