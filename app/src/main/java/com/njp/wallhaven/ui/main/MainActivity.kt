package com.njp.wallhaven.ui.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.njp.wallhaven.R
import com.njp.wallhaven.adapter.TabAdapter
import com.njp.wallhaven.utils.Events
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 主页面
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_skin)

        viewPager.adapter = TabAdapter(
                supportFragmentManager,
                listOf("random" to "推荐", "toplist" to "热门", "latest" to "最新")
        )
        tabLayout.setupWithViewPager(viewPager)
        fab.setOnClickListener {
            EventBus.getDefault().post(Events.EVENT_SCROLL_TO_TOP)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
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
    fun onScrollEvent(events: Events) {
        when (events) {
            Events.EVENT_SCROLL_UP -> fab.show()
            Events.EVENT_SCROLL_DOWN -> fab.hide()
        }
    }


}
