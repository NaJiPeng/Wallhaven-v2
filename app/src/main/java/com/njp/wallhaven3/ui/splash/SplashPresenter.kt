package com.njp.wallhaven3.ui.splash

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.njp.wallhaven3.base.BasePresenter
import com.njp.wallhaven3.base.MyApplication
import com.njp.wallhaven3.repositories.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

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