package com.njp.wallhaven.ui.tag

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.njp.wallhaven.R
import com.njp.wallhaven.adapter.ImagesAdapter
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.Tag
import com.njp.wallhaven.repositories.bean.TagImageInfo
import com.njp.wallhaven.ui.search.text.TextSearchActivity
import com.njp.wallhaven.utils.ActivityController
import com.njp.wallhaven.utils.ColorUtil
import com.njp.wallhaven.utils.ScrollToEvent
import com.njp.wallhaven.utils.ToastUtil
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.activity_tag.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 标签页
 */
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
        ActivityController.getInstance().add(this)
        setContentView(R.layout.activity_tag)
        setP(TagPresenter((this)))

        tag = Tag(
                intent.getStringExtra("tagId"),
                intent.getStringExtra("tagName")
        )

        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { onBackPressed() }
        title = tag.name

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, offset ->
            if (offset == 0) {
                fabSearch.show(true)
                fabStar.show(true)
            } else {
                fabSearch.hide(true)
                fabStar.hide(true)
            }
        })

        val animatorStar = AnimatorInflater.loadAnimator(this, R.animator.animator_fab)
        animatorStar.setTarget(fabStar)
        if (presenter.isTagStared(tag)) fabStar.setImageResource(R.drawable.ic_stared)
        fabStar.setOnClickListener {
            animatorStar.start()
            if (presenter.isTagStared(tag)) {
                presenter.unStarTag(tag)
                ToastUtil.show("已取消收藏标签")
                fabStar.setImageResource(R.drawable.ic_stared_false)
            } else {
                presenter.starTag(tag)
                ToastUtil.show("已收藏标签")

                fabStar.setImageResource(R.drawable.ic_stared)
            }
        }
        val animatorSearch = AnimatorInflater.loadAnimator(this, R.animator.animator_fab)
        animatorSearch.setTarget(fabSearch)
        fabSearch.setOnClickListener {
            animatorSearch.start()
            TextSearchActivity.actionStart(this, tag)
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.itemAnimator = SlideInUpAnimator()
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
        fabStar.colorNormal = color.second
        fabStar.colorPressed = color.second
        fabSearch.colorNormal = color.second
        fabSearch.colorPressed = color.second
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityController.getInstance().remove(this)
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
