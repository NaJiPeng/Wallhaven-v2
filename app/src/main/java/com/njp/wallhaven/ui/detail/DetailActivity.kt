package com.njp.wallhaven.ui.detail

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.njp.wallhaven.R
import com.njp.wallhaven.adapter.DetailImagesAdapter
import com.njp.wallhaven.utils.CommonDataHolder
import kotlinx.android.synthetic.main.activity_detail.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        overridePendingTransition(R.anim.anim_activity_1, R.anim.anim_activity_2)
        current = intent.getIntExtra("current", 0)

        viewPager.adapter = DetailImagesAdapter(supportFragmentManager, CommonDataHolder.getSimpleData())
        viewPager.currentItem = current

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_activity_3, R.anim.anim_activity_4)
    }

    override fun onDestroy() {
        super.onDestroy()
        CommonDataHolder.removeData()
    }

}
