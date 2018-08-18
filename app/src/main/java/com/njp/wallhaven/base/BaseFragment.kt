package com.njp.wallhaven.base

import android.view.ViewGroup
import android.view.LayoutInflater
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.View
import android.support.v4.app.Fragment

/**
 * 带懒加载功能的Fragment
 */
abstract class BaseFragment<V, P : BasePresenter<V>> : Fragment() {

    private var isFirstLoad = false
    var onLazyLoad: (() -> Unit)? = null
    lateinit var presenter: P

    fun setP(presenter: P) {
        this.presenter = presenter
        lifecycle.addObserver(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = createView(inflater, container!!)//让子类实现初始化视图

        isFirstLoad = true//视图创建完成，将变量置为true

        if (userVisibleHint) {//如果Fragment可见进行数据加载
            onLazyLoad?.invoke()
            isFirstLoad = false
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isFirstLoad = false//视图销毁将变量置为false
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isFirstLoad && isVisibleToUser) {//视图变为可见并且是第一次加载
            onLazyLoad?.invoke()
            isFirstLoad = false
        }

    }

    //初始化视图接口，子类必须实现
    abstract fun createView(inflater: LayoutInflater, @Nullable container: ViewGroup): View

}