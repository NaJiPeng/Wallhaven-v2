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
import com.njp.wallhaven.utils.ColorUtil
import com.njp.wallhaven.utils.ScrollEvent
import com.njp.wallhaven.utils.ScrollToEvent
import com.njp.wallhaven.utils.ToastUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.BallPulseFooter
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator
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
        }
    }

    private lateinit var path: String
    private var page = 1
    private val adapter = ImagesAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var footer: BallPulseFooter

    override fun createView(inflater: LayoutInflater, container: ViewGroup): View {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        setP(MainPresenter(this))

        recyclerView = root.findViewById(R.id.recyclerView)
        refreshLayout = root.findViewById(R.id.refreshLayout)
        footer = root.findViewById(R.id.footer)

        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.itemAnimator = FlipInTopXAnimator()
        recyclerView.adapter = SlideInBottomAnimationAdapter(adapter)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                EventBus.getDefault().post(if (dy < 0) ScrollEvent.EVENT_SCROLL_UP else ScrollEvent.EVENT_SCROLL_DOWN)
            }
        })

        refreshLayout.setOnRefreshListener { presenter.onRefreshImages(path) }
        refreshLayout.setOnLoadMoreListener { presenter.onLoadMoreImages(path, ++page) }

        onLazyLoad = {
            refreshLayout.autoRefresh()
        }

        EventBus.getDefault().register(this)
        onChangeSkin(ColorUtil.getInstance().getCurrentColor())

        return root
    }

    override fun onRefreshImages(images: List<SimpleImageInfo>) {
        adapter.setData(images)
        page = 1
        refreshLayout.finishRefresh(true)
        refreshLayout.setEnableLoadMore(true)
    }

    override fun onRefreshImagesFail(msg: String) {
        ToastUtil.show(msg)
        refreshLayout.finishRefresh(false)
    }

    override fun onLoadMoreImages(images: List<SimpleImageInfo>) {
        adapter.addData(images)
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
        refreshLayout.setEnableLoadMore(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScrollToUp(event: ScrollToEvent) {
        if (userVisibleHint) {
            recyclerView.let {
                if (event.isSmooth) {
                    it.smoothScrollToPosition(event.position)
                } else {
                    it.scrollToPosition(event.position)
                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeSkin(color: Pair<String, Int>) {
        footer.setAnimatingColor(color.second)
    }

}