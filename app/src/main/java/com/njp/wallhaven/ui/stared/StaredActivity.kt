package com.njp.wallhaven.ui.stared

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.jaeger.library.StatusBarUtil
import com.njp.wallhaven.R
import com.njp.wallhaven.adapter.ImagesAdapter
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.utils.ColorUtil
import com.njp.wallhaven.utils.ToastUtil
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator
import kotlinx.android.synthetic.main.activity_stared.*

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
        recyclerView.itemAnimator = FlipInTopXAnimator()
        recyclerView.adapter = SlideInBottomAnimationAdapter(adapter)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) fab.hide() else fab.show()
            }
        })

        refreshLayout.setOnRefreshListener { presenter.getStaredImages() }

        fab.setOnClickListener { recyclerView.scrollToPosition(0) }

        onChangeColor(ColorUtil.getInstance().getCurrentColor())

    }

    override fun onStart() {
        super.onStart()
        refreshLayout.autoRefresh()
    }


    override fun onStaredImages(images: List<SimpleImageInfo>) {
        adapter.setImages(images)
        refreshLayout.finishRefresh()
    }

    override fun onNoStaredImages() {
        ToastUtil.show("你还没有收藏任何图片呢")
        refreshLayout.finishRefresh()
    }

    private fun onChangeColor(color: Pair<String, Int>) {
        StatusBarUtil.setColorNoTranslucent(this, color.second)
        toolBar.setBackgroundColor(color.second)
        fab.backgroundTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(color.second)
        )

    }

}
