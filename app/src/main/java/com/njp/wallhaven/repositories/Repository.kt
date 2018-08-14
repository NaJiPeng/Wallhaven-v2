package com.njp.wallhaven.repositories

import com.njp.wallhaven.bean.SplashImages
import com.njp.wallhaven.repositories.local.LocalDao
import com.njp.wallhaven.repositories.network.NetworkInstance
import com.njp.wallhaven.repositories.network.NetworkInstance.retrofit

/**
 * 应用数据来源唯一接口
 */
class Repository private constructor() {

    companion object {

        private var instance: Repository? = null

        fun getInstance(): Repository {
            if (instance == null) {
                instance = Repository()
            }
            return instance!!
        }

    }

    private val service = retrofit.create(NetworkInstance.RetrofitService::class.java)
    private val dao = LocalDao()

    fun getSplashImagesFromInternet() = service.getSplashImages()

    fun getSplashImagesFromLocal(): SplashImages? = dao.getSplashImages()

    fun updateSplashimageToLocal(splashImages: SplashImages) {
        dao.saveSplashImages(splashImages)
    }

}