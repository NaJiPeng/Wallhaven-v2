package com.njp.wallhaven.repositories

import com.njp.wallhaven.repositories.bean.SplashImages
import com.njp.wallhaven.repositories.network.NetworkInstance
import com.njp.wallhaven.repositories.network.NetworkInstance.retrofit
import com.raizlabs.android.dbflow.sql.language.SQLite

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

    fun getImages(path: String, page: Int) = service.getImages(path, page)

    fun getSplashImagesFromDB(): SplashImages? {
        val list = SQLite.select()
                .from(SplashImages::class.java)
                .queryList()
        return if (list.isNotEmpty()) list[0] else null
    }

    fun updateSplashImageToDB(splashImages: SplashImages) {
        SQLite.select()
                .from(SplashImages::class.java)
                .queryList()
                .forEach { it.delete() }
        splashImages.save()
    }

}