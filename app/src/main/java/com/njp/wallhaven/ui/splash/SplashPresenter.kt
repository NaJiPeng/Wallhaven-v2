package com.njp.wallhaven.ui.splash

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.base.MyApplication
import com.njp.wallhaven.repositories.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log

class SplashPresenter(view: SplashContract.View) : BasePresenter<SplashContract.View>(view), SplashContract.Presenter {

    override fun getSplashImages() {
        val images = Repository.getInstance().getSplashImagesFromDB()
        if (images.isNotEmpty()) {
            images.shuffle()
            view?.onSplashImages(images[0])
            images.forEach {
                Glide.with(MyApplication.application)
                        .load(it.url)
                        .apply(RequestOptions().apply {
                            diskCacheStrategy(DiskCacheStrategy.DATA)
                        })
                        .preload(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            }
        } else {
            view?.onNoSplashImage()
        }
    }


    @SuppressLint("CheckResult")
    override fun updateSplashImages() {
        Repository.getInstance().getSplashImages()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        {
                            Repository.getInstance().updateSplashImageToDB(it)
                        },
                        {}
                )

    }

    override fun startTimer() {
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view?.onTimer()
                }?.let { addDisposable(it) }
    }


}