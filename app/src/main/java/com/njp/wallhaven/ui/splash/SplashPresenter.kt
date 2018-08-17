package com.njp.wallhaven.ui.splash

import android.annotation.SuppressLint
import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.repositories.bean.SplashImages
import com.njp.wallhaven.repositories.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SplashPresenter(view: SplashContract.View) : BasePresenter<SplashContract.View>(view), SplashContract.Presenter {

    override fun getSplashImage() {
        val images = Repository.getInstance().getSplashImagesFromDB()?.images
        view?.onSplashImage(images?.get(Random().nextInt(images.size)))
    }


    @SuppressLint("CheckResult")
    override fun updateSplashImages() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val date = Repository.getInstance().getSplashImagesFromDB()?.date
        val now = sdf.format(Date())
        if (date != now) {
            Repository.getInstance().getImages("", 0)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val doc = Jsoup.parse(it.string())
                        val elements = doc.select("#featured a")
                        val images = elements.map {
                            val href = it.attr("href")
                            val url = "http:" + it.child(0).attr("src")
                            SimpleImageInfo().apply {
                                this.href = href
                                this.url = url
                            }
                        }
                        Repository.getInstance().updateSplashImageToDB(
                                SplashImages().apply {
                                    this.date = now
                                    this.images = images
                                }
                        )
                    }
        }
    }

    override fun startTimer() {
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.onTimer() }
                .let { addDisposable(it) }
    }


}