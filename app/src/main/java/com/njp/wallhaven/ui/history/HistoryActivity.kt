package com.njp.wallhaven.ui.history

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.github.ybq.android.spinkit.SpinKitView
import com.jaeger.library.StatusBarUtil
import com.njp.wallhaven.R
import com.njp.wallhaven.adapter.ImagesAdapter
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.utils.ColorUtil
import com.njp.wallhaven.utils.ScrollToEvent
import com.njp.wallhaven.utils.ToastUtil
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator
import kotlinx.android.synthetic.main.activity_history.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HistoryActivity : HistoryContract.View, BaseActivity<HistoryContract.View, HistoryPresenter>() {

    private val adapter = ImagesAdapter()
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setP(HistoryPresenter(this))

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

        refreshLayout.setOnRefreshListener { presenter.refreshImages() }
        refreshLayout.setOnLoadMoreListener { presenter.loadMoreImages() }

        fab.setOnClickListener { recyclerView.smoothScrollToPosition(0) }

        loadingDialog = Dialog(this, R.style.dialog)
        val dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_loading, null)
        loadingDialog.setContentView(dialogView)
        val spinKit = dialogView.findViewById<SpinKitView>(R.id.spinKit)
        spinKit.setColor(ColorUtil.getInstance().getCurrentColor().second)
        loadingDialog.setCancelable(false)


        onChangeColor(ColorUtil.getInstance().getCurrentColor())

        refreshLayout.autoRefresh()

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when (item?.itemId) {
            R.id.clear -> {
                AlertDialog.Builder(this)
                        .setMessage("是否清空浏览记录？")
                        .setPositiveButton("确定") { p0, _ ->
                            p0?.dismiss()
                            loadingDialog.show()
                            Thread {
                                presenter.clearHistoryImages()
                                runOnUiThread {
                                    adapter.clear()
                                    loadingDialog.dismiss()
                                    ToastUtil.show("已清空")
                                }
                            }.start()
                        }.setNegativeButton("取消") { p0, _ ->
                            p0?.dismiss()
                        }.show()
            }
        }
        return true
    }

    override fun onRefreshImages(data: List<SimpleImageInfo>) {
        adapter.setData(data)
        refreshLayout.finishRefresh()
        refreshLayout.setEnableLoadMore(true)
    }

    override fun onNoImages() {
        ToastUtil.show("无浏览记录")
        refreshLayout.finishRefresh()
        refreshLayout.setEnableLoadMore(false)
    }

    override fun onLoadMoreImages(data: List<SimpleImageInfo>) {
        adapter.addData(data)
        refreshLayout.finishLoadMore()
    }

    override fun onNoMoreImages() {
        ToastUtil.show("没有更多啦 >_<")
        refreshLayout.finishLoadMore()
        refreshLayout.setEnableLoadMore(false)
    }

    private fun onChangeColor(color: Pair<String, Int>) {
        StatusBarUtil.setColorNoTranslucent(this, color.second)
        toolBar.setBackgroundColor(color.second)
        fab.backgroundTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(color.second)
        )
        footer.setAnimatingColor(color.second)
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
