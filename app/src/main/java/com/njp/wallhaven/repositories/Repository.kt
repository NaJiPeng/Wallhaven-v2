package com.njp.wallhaven.repositories

import com.njp.wallhaven.repositories.bean.*
import com.njp.wallhaven.repositories.network.NetworkInstance
import com.njp.wallhaven.repositories.network.NetworkInstance.retrofit
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.kotlinextensions.whereExists
import com.raizlabs.android.dbflow.sql.language.SQLite
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
                            this.id = id
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
                            this.id = id
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
    fun getSplashImagesFromDB(): MutableList<SimpleImageInfo> {
        return SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isSPlash.eq(true))
                .queryList()
    }

    /**
     * 更新本地数据库中的闪屏页图片
     */
    fun updateSplashImageToDB(images: List<SimpleImageInfo>) {
        SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isSPlash.eq(true))
                .queryList().forEach {
                    it.isSPlash = false
                    it.selfCheck()
                }
        images.forEach {
            if (it.exists()) {
                SQLite.select()
                        .from(SimpleImageInfo::class.java)
                        .where(SimpleImageInfo_Table.id.eq(it.id))
                        .querySingle()?.apply {
                            this.isSPlash = true
                            this.save()
                        }


            } else {
                it.isSPlash = true
                it.save()
            }
        }
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
        SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isStared.eq(true) and SimpleImageInfo_Table.id.eq(image.id))
                .querySingle()?.apply {
                    this.isStared = false
                    this.save()
                    this.selfCheck()
                }
    }

    /**
     * 判断图片是否收藏
     */
    fun isStared(image: SimpleImageInfo): Boolean {
        return SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isStared.eq(true) and SimpleImageInfo_Table.id.eq(image.id))
                .querySingle() != null

    }

    /**
     * 获取收藏图片列表
     */
    fun getStartedImages(page: Int): List<SimpleImageInfo> {
        return SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isStared.eq(true))
                .limit(24)
                .offset(page * 24)
                .queryList()
    }

    /**
     * 添加浏览记录
     */
    fun addHistory(image: SimpleImageInfo, date: String) {
        if (image.exists()) {
            SQLite.select()
                    .from(SimpleImageInfo::class.java)
                    .where(SimpleImageInfo_Table.id.eq(image.id))
                    .querySingle()?.apply {
                        this.isHistory = true
                        this.date = date
                        this.save()
                    }
        } else {
            image.isHistory = true
            image.date = date
            image.save()
        }
    }

    /**
     * 获取历史记录列表
     */
    fun getHistory(page: Int): List<SimpleImageInfo> {
        return SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isHistory.eq(true))
                .orderBy(SimpleImageInfo_Table.date, false)
                .limit(24)
                .offset(page * 24)
                .queryList()
    }

    /**
     * 清空浏览记录
     */
    fun clearHistory() {
        SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isHistory.eq(true))
                .queryList()
                .forEach {
                    it.isHistory = false
                    it.save()
                    it.selfCheck()
                }
    }

    /**
     * 从网络上获取tag相关图片信息
     */
    fun getTagImageInfo(tagId: Int): Observable<TagImageInfo> {
        return service.getTagImageInfo(tagId)
                .map {
                    val doc = Jsoup.parse(it.string())
                    val titleImageUrl = "http:" + doc.select("#tag-header-wrapper")[0]
                            .attr("style")
                            .split("(", ")")[1]
                    val images = doc.select("#tag-thumbs figure")
                            .map { element ->
                                val id = element.attr("data-wallpaper-id").toInt()
                                val url = element.select("img")[0].attr("data-src")
                                return@map SimpleImageInfo().apply {
                                    this.id = id
                                    this.url = url
                                }
                            }
                    return@map TagImageInfo(titleImageUrl, images)
                }
    }
}