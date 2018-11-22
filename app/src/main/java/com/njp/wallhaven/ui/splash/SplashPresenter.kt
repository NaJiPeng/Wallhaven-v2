package com.njp.wallhaven.ui.splash

import android.annotation.SuppressLint
import com.njp.wallhaven.base.BasePresenter
import com.njp.wallhaven.repositories.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SplashPresenter(view: SplashContract.View) : BasePresenter<SplashContract.View>(view), SplashContract.Presenter {

    override fun getSplashImages() {
        val images = Repository.getInstance().getSplashImagesFromDB()
        if (images.isNotEmpty()) {
            images.shuffle()
            view?.onSplashImages(images)
        }else{
            view?.onNoSplashImage()
        }
    }


    @SuppressLint("CheckResult")
    override fun updateSplashImages() {
        Repository.getInstance().getSplashImages()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe (
                        { Repository.getInstance().updateSplashImageToDB(it) },
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