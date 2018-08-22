package com.njp.wallhaven.repositories

import com.njp.wallhaven.repositories.bean.*
import com.njp.wallhaven.repositories.network.NetworkInstance
import com.njp.wallhaven.repositories.network.NetworkInstance.retrofit
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction
import io.reactivex.Observable
import org.jsoup.Jsoup

/**
 * 应用数据交互唯一接口
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

    /**
     * 从网络上获取闪屏页图片
     */
    fun getSplashImages(): Observable<List<SimpleImageInfo>> {
        return service.getImages("", 0)
                .map { it ->
                    val doc = Jsoup.parse(it.string())
                    val elements = doc.select("#featured a")
                    return@map elements.map { element ->
                        val id = element.attr("href").split("/").last().toInt()
                        val url = "http:" + element.child(0).attr("src")
                        SimpleImageInfo().apply {
                            imageId = id
                            this.url = url
                        }
                    }
                }
    }

    /**
     * 从网络上获取主页图片
     */
    fun getImages(path: String, page: Int): Observable<List<SimpleImageInfo>> {
        return service.getImages(path, page)
                .map {
                    val doc = Jsoup.parse(it.string())
                    val elements = doc.select("#thumbs figure")
                    val images = elements.map { element ->
                        val url = element.getElementsByTag("img")[0].attr("data-src")
                        val id = element.attr("data-wallpaper-id").toInt()
                        SimpleImageInfo().apply {
                            this.url = url
                            this.imageId = id
                        }
                    }
                    return@map images
                }
    }

    /**
     * 从网络上获取详情大图
     */
    fun getDetailImage(id: Int): Observable<DetailImageInfo> {
        return service.getDetailImage(id)
                .map {
                    val doc = Jsoup.parse(it.string())
                    val url = "http:" + doc.select("#wallpaper").last().attr("src")
                    val resolution = doc.select(".showcase-resolution")[0].text()
                    val tags = doc.select("#tags")[0].children().map { element ->
                        val id = element.attr("data-tag-id").toInt()
                        val name = element.child(0).text()
                        return@map Tag(id, name)
                    }
                    return@map DetailImageInfo(url, resolution, tags)
                }
    }

    /**
     * 从本地数据库中获取闪屏页图片
     */
    fun getSplashImagesFromDB(): SplashImages? {
        val list = SQLite.select()
                .from(SplashImages::class.java)
                .queryList()
        return if (list.isNotEmpty()) list[0] else null
    }

    /**
     * 更新本地数据库中的闪屏页图片
     */
    fun updateSplashImageToDB(splashImages: SplashImages) {
        SQLite.delete()
                .from(SplashImages::class.java)
                .execute()
        splashImages.save()
    }

    /**
     * 收藏图片到本地数据库
     */
    fun starImage(image: SimpleImageInfo) {
        image.isStared = true
        image.save()
    }

    /**
     * 取消收藏
     */
    fun unStarImage(image: SimpleImageInfo) {
        SQLite.delete()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isStared.eq(true) and SimpleImageInfo_Table.imageId.eq(image.imageId))
                .queryList()
                .forEach { it.delete() }
    }

    /**
     * 判断图片是否收藏
     */
    fun isStared(image: SimpleImageInfo): Boolean {
        val images = SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isStared.eq(true) and SimpleImageInfo_Table.imageId.eq(image.imageId))
                .queryList()
        return images.isNotEmpty()
    }

    /**
     * 获取收藏图片列表
     */
    fun getStartedImages(): List<SimpleImageInfo> {
        return SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isStared.eq(true))
                .queryList()
                .asReversed()
    }

    /**
     * 添加浏览记录
     */
    fun addHistory(image: SimpleImageInfo, date: String) {
        val historyImages = SQLite.select()
                .from(HistoryImages::class.java)
                .where(HistoryImages_Table.date.eq(date))
                .queryList()
        val historyImage = if (historyImages.isNotEmpty()) historyImages.last() else HistoryImages().apply { this.date = date }
        historyImage.apply {
            this.images = this.images ?: ArrayList()
            this.images?.add(image)
            this.save()
        }
    }

    /**
     * 获取历史记录列表
     */
    fun getHistory(): List<HistoryImages> {
        return SQLite.select()
                .from(HistoryImages::class.java)
                .queryList()
                .asReversed()
    }

    /**
     * 清空浏览记录
     */
    fun clearHistory() {
        SQLite.select()
                .from(HistoryImages::class.java)
                .queryList()
                .forEach { it.delete() }
    }

}