package com.njp.wallhaven.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

open class BasePresenter<V>(protected var view: V?) : LifecycleObserver {

    private val disposables = ArrayList<Disposable>()

    fun addDisposable(disposable: Disposable){
        disposables.add(disposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        view = null
        disposables.forEach { it.dispose() }
        disposables.clear()
    }

}