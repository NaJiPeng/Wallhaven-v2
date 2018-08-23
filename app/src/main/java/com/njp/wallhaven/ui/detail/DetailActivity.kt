package com.njp.wallhaven.ui.detail

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.njp.wallhaven.R
import com.njp.wallhaven.adapter.DetailImagesAdapter
import com.njp.wallhaven.repositories.Repository
import com.njp.wallhaven.utils.CommonDataHolder
import com.njp.wallhaven.utils.ScrollToEvent
import kotlinx.android.synthetic.main.activity_detail.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

/**
 * 详情大图页
 */
class DetailActivity : AppCompatActivity() {

    companion object {
        fun actionStart(context: Context, current: Int) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("current", current)
            context.startActivity(intent)
        }
    }

    private var current: Int = 0
    private val today = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        overridePendingTransition(R.anim.anim_activity_1, R.anim.anim_activity_2)
        current = intent.getIntExtra("current", 0)

        viewPager.adapter = DetailImagesAdapter(supportFragmentManager, CommonDataHolder.getSimpleData())
        Repository.getInstance().addHistory(CommonDataHolder.getImage(current)!!, today)
        viewPager.currentItem = current
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                current = p0
                Repository.getInstance().addHistory(CommonDataHolder.getImage(p0)!!, today)
            }

        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_activity_3, R.anim.anim_activity_4)
    }

    override fun onDestroy() {
        super.onDestroy()
        CommonDataHolder.removeData()
        EventBus.getDefault().postSticky(ScrollToEvent(current,true))
    }

}
