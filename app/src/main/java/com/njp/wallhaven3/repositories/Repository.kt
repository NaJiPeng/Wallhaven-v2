package com.njp.wallhaven3.repositories

import android.util.Log
import com.njp.wallhaven3.repositories.bean.*
import com.njp.wallhaven3.repositories.network.NetworkInstance
import com.njp.wallhaven3.repositories.network.NetworkInstance.retrofit
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jsoup.Jsoup
import java.io.File

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
                .map {
                    val doc = Jsoup.parse(it.string())
                    val elements = doc.select(".lg-thumb a")
                    return@map elements.map { element ->
                        element.attr("href").split("/").last()
                    }
                }.flatMap {
                    Observable.create<String> { emitter ->
                        it.forEach {
                            emitter.onNext(it)
                        }
                    }
                }.flatMap { id ->
                    return@flatMap getDetailImage(id).map {
                        SimpleImageInfo(id, it.url)
                    }
                }.buffer(4)
    }

    /**
     * 从网络上获取主页图片
     */
    fun getImages(path: String, page: Int): Observable<List<SimpleImageInfo>> {
        return service.getImages(path, page)
                .map {
                    val doc = Jsoup.parse(it.string())
                    val elements = doc.select("#thumbs figure:not(.thumb-nsfw)")
                    val images = elements.map { element ->
                        val url = element.getElementsByTag("img")[0].attr("data-src")
                        val id = element.attr("data-wallpaper-id")
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
    fun getDetailImage(id: String): Observable<DetailImageInfo> {
        return service.getDetailImage(id)
                .map {
                    val doc = Jsoup.parse(it.string())
                    val url = doc.select("#wallpaper").last().attr("src")
                    val resolution = doc.select(".showcase-resolution")[0].text()
                    val tags = doc.select("#tags")[0].children().map { element ->
                        val id = element.attr("data-tag-id")
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
                .orderBy(SimpleImageInfo_Table.time, false)
                .limit(24)
                .offset(page * 24)
                .queryList()
    }

    /**
     * 添加浏览记录
     */
    fun addHistory(image: SimpleImageInfo, time: Long) {
        if (image.exists()) {
            SQLite.select()
                    .from(SimpleImageInfo::class.java)
                    .where(SimpleImageInfo_Table.id.eq(image.id))
                    .querySingle()?.apply {
                        this.isHistory = true
                        this.time = time
                        this.save()
                    }
        } else {
            image.isHistory = true
            image.time = time
            image.save()
        }
    }

    /**
     * 获取历史记录列表
     */
    fun getHistoryImages(page: Int): List<SimpleImageInfo> {
        return SQLite.select()
                .from(SimpleImageInfo::class.java)
                .where(SimpleImageInfo_Table.isHistory.eq(true))
                .orderBy(SimpleImageInfo_Table.time, false)
                .limit(24)
                .offset(page * 24)
                .queryList()
    }

    /**
     * 清空浏览记录
     */
    fun clearHistoryImages() {
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
    fun getTagImageInfo(tagId: String): Observable<TagImageInfo> {
        return service.getTagImageInfo(tagId)
                .map {
                    val doc = Jsoup.parse(it.string())
                    val titleImage = doc.select("#tag-header-wrapper")[0]
                    val titleImageUrl = titleImage.attr("style")
                            .split("(", ")")[1]
                    val images = doc.select("#tag-thumbs figure:not(.thumb-nsfw)")
                            .map { element ->
                                val id = element.attr("data-wallpaper-id")
                                val url = element.select("img")[0].attr("data-src")
                                return@map SimpleImageInfo().apply {
                                    this.id = id
                                    this.url = url
                                }
                            }
                    return@map TagImageInfo(titleImageUrl, images)
                }
    }

    /**
     * 收藏Tag
     */
    fun starTag(tag: Tag) {
        tag.save()
    }

    /**
     * 取消收藏Tag
     */
    fun unStarTag(tag: Tag) {
        tag.delete()
    }

    /**
     * 判断Tag是否已收藏
     */
    fun isTagStared(tag: Tag) = tag.exists()

    /**
     * 获取收藏tag列表
     */
    fun getTags(): List<Tag> {
        return SQLite.select()
                .from(Tag::class.java)
                .queryList()
    }

    /**
     * 根据关键字查找图片
     */
    fun searchByText(
            q: String,
            ratios: String,
            colors: String,
            sorting: String,
            topRange: String,
            categories: String,
            page: Int
    ): Observable<List<SimpleImageInfo>> {
        return service.searchByText(q, ratios, colors, sorting, topRange, categories, page).map {
            val doc = Jsoup.parse(it.string())
            val images = doc.select("#thumbs figure:not(.thumb-nsfw)")
                    .map { element ->
                        val id = element.attr("data-wallpaper-id")
                        val url = element.select("img")[0].attr("data-src")
                        return@map SimpleImageInfo().apply {
                            this.id = id
                            this.url = url
                        }
                    }
            return@map images
        }
    }

    /**
     * 保存搜索历史
     */
    fun saveHistory(history: History) {
        history.save()
    }

    /**
     * 获取搜索历史记录
     */
    fun getSearchHistory(): List<History> {
        return SQLite.select()
                .from(History::class.java)
                .orderBy(History_Table.time, false)
                .queryList()
    }

    /**
     * 删除搜索记录
     */
    fun deleteHistory(history: History) {
        history.delete()
    }

    /**
     * 清除搜索记录
     */
    fun clearHistory() {
        SQLite.select()
                .from(History::class.java)
                .queryList()
                .forEach { it.delete() }
    }

    /**
     * 以图搜图
     */
    fun searchByImage(image: File): Observable<List<SimpleImageInfo>> {
        Log.i("wwww", "${image.name}")
        val body = RequestBody.create(MediaType.parse("multipart/form-data"), image)
        val part = MultipartBody.Part.createFormData("search_image", image.name, body)
        return service.searchByImage(part).map {
            val doc = Jsoup.parse(it.string())
            val images = doc.select("#thumbs figure:not(.thumb-nsfw)")
                    .map { element ->
                        val id = element.attr("data-wallpaper-id")
                        val url = element.select("img")[0].attr("data-src")
                        return@map SimpleImageInfo().apply {
                            this.id = id
                            this.url = url
                        }
                    }
            return@map images
        }
    }


}