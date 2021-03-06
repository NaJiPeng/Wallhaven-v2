package com.njp.wallhaven3.ui.stared

import android.animation.AnimatorInflater
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.jaeger.library.StatusBarUtil
import com.njp.wallhaven3.R
import com.njp.wallhaven3.adapter.ImagesAdapter
import com.njp.wallhaven3.base.BaseActivity
import com.njp.wallhaven3.repositories.bean.SimpleImageInfo
import com.njp.wallhaven3.utils.ColorUtil
import com.njp.wallhaven3.utils.ScrollToEvent
import com.njp.wallhaven3.utils.ToastUtil
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.activity_stared.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StaredActivity : BaseActivity<StaredContract.View, StaredPresenter>(), StaredContract.View {

    private val adapter = ImagesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stared)
        setP(StaredPresenter(this))

        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { onBackPressed() }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.itemAnimator = SlideInUpAnimator()
        recyclerView.adapter = SlideInBottomAnimationAdapter(adapter)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) fab.hide(true) else fab.show(true)
            }
        })

        refreshLayout.setOnRefreshListener { presenter.refreshImages() }
        refreshLayout.setOnLoadMoreListener { presenter.loadMoreImages() }

        val animator = AnimatorInflater.loadAnimator(this,R.animator.animator_fab)
        animator.setTarget(fab)
        fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
            animator.start()
        }

        onChangeColor(ColorUtil.getInstance().getCurrentColor())

        refreshLayout.autoRefresh()

    }


    override fun onRefreshImages(images: List<SimpleImageInfo>) {
        adapter.setData(images)
        refreshLayout.finishRefresh()
        refreshLayout.setEnableLoadMore(true)
    }

    override fun onNoImages() {
        adapter.clear()
        ToastUtil.show("你还没有收藏任何图片呢")
        refreshLayout.finishRefresh()
        refreshLayout.setEnableLoadMore(false)
    }

    override fun onLoadMoreImages(images: List<SimpleImageInfo>) {
        adapter.addData(images)
        refreshLayout.finishLoadMore()
    }

    override fun onNoMoreImages() {
        refreshLayout.finishLoadMore()
        ToastUtil.show("没有更多了 >_<")
        refreshLayout.setEnableLoadMore(false)
    }


    private fun onChangeColor(color: Pair<String, Int>) {
        StatusBarUtil.setColorNoTranslucent(this, color.second)
        toolBar.setBackgroundColor(color.second)
        fab.colorPressed = color.second
        fab.colorNormal = color.second
        footer.setAnimatingColor(color.second)

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScrollToUp(event: ScrollToEvent) {
        recyclerView.let {
            if (event.isSmooth) {
                it.smoothScrollToPosition(event.position)
            } else {
                it.scrollToPosition(event.position)
            }
        }
    }

}
