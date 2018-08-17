package com.njp.wallhaven.base

import android.support.v7.app.AppCompatActivity

abstract class BaseActivity<V, P : BasePresenter<V>> : AppCompatActivity() {

    lateinit var presenter: P

    fun setP(presenter: P) {
        this.presenter = presenter
        lifecycle.addObserver(presenter)
    }

}