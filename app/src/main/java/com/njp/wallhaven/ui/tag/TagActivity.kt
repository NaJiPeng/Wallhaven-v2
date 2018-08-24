package com.njp.wallhaven.ui.tag

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jaeger.library.StatusBarUtil
import com.njp.wallhaven.R
import com.njp.wallhaven.adapter.ImagesAdapter
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.Tag
import com.njp.wallhaven.repositories.bean.TagImageInfo
import com.njp.wallhaven.utils.ColorUtil
import com.njp.wallhaven.utils.ToastUtil
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator
import kotlinx.android.synthetic.main.activity_tag.*

class TagActivity : BaseActivity<TagContract.View, TagPresenter>(), TagContract.View {

    companion object {
        fun actionStart(context: Context, tag: Tag) {
            val intent = Intent(context, TagActivity::class.java)
            intent.putExtra("tagId", tag.id)
            intent.putExtra("tagName", tag.name)
            context.startActivity(intent)
        }
    }

    private lateinit var tag: Tag
    private val adapter = ImagesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)
        setP(TagPresenter((this)))

        tag = Tag(
                intent.getIntExtra("tagId", 0),
                intent.getStringExtra("tagName")
        )

        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { onBackPressed() }
        title = tag.name

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.itemAnimator = FlipInTopXAnimator()
        recyclerView.adapter = SlideInBottomAnimationAdapter(adapter)

        refreshLayout.setOnRefreshListener { presenter.getTagImageInfo(tag.id) }

        onChangeColor(ColorUtil.getInstance().getCurrentColor())

        refreshLayout.autoRefresh()

    }

    override fun onGetTagImageInfo(imageInfo: TagImageInfo) {
        Glide.with(this)
                .load(imageInfo.titleImageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageTag)
        adapter.setData(imageInfo.images)
        refreshLayout.finishRefresh()
    }

    override fun onGetTagImageInfoFail(msg: String) {
        ToastUtil.show(msg)
        refreshLayout.finishRefresh()
    }

    private fun onChangeColor(color: Pair<String, Int>) {
        appBarLayout.setBackgroundColor(color.second)
        collapsingLayout.setContentScrimColor(color.second)
        collapsingLayout.statusBarScrim = ColorDrawable(color.second)
        fabStar.backgroundTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(color.second)
        )
        fabSearch.backgroundTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(color.second)
        )
    }

}
