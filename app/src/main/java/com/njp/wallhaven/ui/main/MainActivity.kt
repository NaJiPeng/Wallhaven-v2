package com.njp.wallhaven.ui.main

import android.content.res.ColorStateList
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import com.jaeger.library.StatusBarUtil
import com.njp.wallhaven.R
import com.njp.wallhaven.adapter.TabAdapter
import com.njp.wallhaven.utils.ScrollEvents
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import com.njp.wallhaven.adapter.BottomSheetAdapter
import com.njp.wallhaven.utils.ColorUtil
import com.njp.wallhaven.utils.SPUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 主页面
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_skin)

        viewPager.adapter = TabAdapter(
                supportFragmentManager,
                listOf("random" to "推荐", "toplist" to "热门", "latest" to "最新")
        )
        viewPager.offscreenPageLimit = 3
        tabLayout.setupWithViewPager(viewPager)

        fab.setOnClickListener {
            EventBus.getDefault().post(ScrollEvents.EVENT_SCROLL_TO_TOP)
        }
        EventBus.getDefault().register(this)

        initBottomSheet()
        toolBar.setNavigationOnClickListener {
            bottomSheetDialog.show()
        }

        onChangeSkin(ColorUtil.getInstance().getCurrentColor())

    }

    private fun initBottomSheet() {
        val recyclerView = View.inflate(this, R.layout.bottomsheet_colors, null) as RecyclerView
        val adapter = BottomSheetAdapter(ColorUtil.getInstance().list) {
            SPUtil.getInstance().putString("color", it.first)
            EventBus.getDefault().post(it)
            bottomSheetDialog.dismiss()
        }

        recyclerView.layoutManager = GridLayoutManager(this, 5)
        recyclerView.adapter = adapter
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScrollEvent(events: ScrollEvents) {
        when (events) {
            ScrollEvents.EVENT_SCROLL_UP -> fab.show()
            ScrollEvents.EVENT_SCROLL_DOWN -> fab.hide()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeSkin(color: Pair<String, Int>) {
        StatusBarUtil.setColorNoTranslucent(this, color.second)
        toolBar.setBackgroundColor(color.second)
        tabLayout.setBackgroundColor(color.second)
        fab.backgroundTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(color.second)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
