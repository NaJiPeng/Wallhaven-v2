package com.njp.wallhaven.ui.main

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.njp.wallhaven.R
import com.njp.wallhaven.adapter.ImagesAdapter
import com.njp.wallhaven.base.BaseFragment
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.utils.Events
import com.njp.wallhaven.utils.ToastUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import jp.wasabeef.recyclerview.adapters.*
import jp.wasabeef.recyclerview.animators.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 主页图片展示页
 */
class MainFragment : BaseFragment<MainContract.View, MainPresenter>(), MainContract.View {

    companion object {
        fun create(path: String) = MainFragment().apply {
            this.path = path
            setP(MainPresenter(this))
        }
    }

    private lateinit var path: String
    private var page = 1
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SmartRefreshLayout
    private val adapter = ImagesAdapter()

    override fun initView(inflater: LayoutInflater, container: ViewGroup): View {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        refreshLayout = root.findViewById(R.id.refreshLayout)
        recyclerView = root.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.itemAnimator = FlipInTopXAnimator()
        recyclerView.adapter = SlideInBottomAnimationAdapter(adapter)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                EventBus.getDefault().post(if (dy > 0) Events.EVENT_SCROLL_UP else Events.EVENT_SCROLL_DOWN)
            }
        })

        refreshLayout.setOnRefreshListener { presenter.onRefreshImages(path) }
        refreshLayout.setOnLoadMoreListener { presenter.onLoadMoreImages(path, ++page) }

        onLazyLoad = {
            refreshLayout.autoRefresh()
        }

        return root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            EventBus.getDefault().register(this)
        } else {
            EventBus.getDefault().unregister(this)
        }
    }


    override fun onRefreshImages(images: List<SimpleImageInfo>) {
        adapter.setImages(images)
        page = 1
        refreshLayout.finishRefresh(true)
        refreshLayout.setNoMoreData(false)
    }

    override fun onRefreshImagesFail(msg: String) {
        ToastUtil.show(msg)
        refreshLayout.finishRefresh(false)
    }

    override fun onLoadMoreImages(images: List<SimpleImageInfo>) {
        adapter.addImages(images)
        refreshLayout.finishLoadMore(true)
    }

    override fun onLoadMoreImagesFail(msg: String) {
        ToastUtil.show(msg)
        page--
        refreshLayout.finishLoadMore(false)
    }

    override fun onNoMore() {
        ToastUtil.show("没有更多啦 >_<")
        page--
        refreshLayout.finishLoadMore(false)
        refreshLayout.setNoMoreData(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScrollToUp(event: Events) {
        if (event == Events.EVENT_SCROLL_TO_TOP) {
            recyclerView.smoothScrollToPosition(0)
        }
    }

}