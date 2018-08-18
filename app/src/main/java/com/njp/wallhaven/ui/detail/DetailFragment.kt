package com.njp.wallhaven.ui.detail

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.chip.Chip
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED
import android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.njp.wallhaven.R
import com.njp.wallhaven.base.BaseFragment
import com.njp.wallhaven.repositories.bean.DetailImageInfo
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.utils.ColorUtil
import com.njp.wallhaven.utils.ScrollToEvent
import com.njp.wallhaven.utils.ToastUtil
import com.njp.wallhaven.utils.loadWithProgress
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.EventBus

class DetailFragment : BaseFragment<DetailContract.View, DetailPresenter>(), DetailContract.View {

    companion object {
        fun create(image: SimpleImageInfo, size: Int, position: Int) = DetailFragment().apply {
            this.image = image
            this.size = size
            this.position = position + 1
            setP(DetailPresenter(this))
        }
    }

    private lateinit var image: SimpleImageInfo
    private var size = 0
    private var position = 0


    override fun createView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textIndicate.text = "$position/$size"
        photoView.maximumScale *= 2
        photoView.setOnPhotoTapListener { _, _, _ ->
            activity?.finish()
        }
        Glide.with(context!!).load(image.url).into(photoView)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(p0: View, p1: Int) {
                when (p1) {
                    STATE_COLLAPSED -> imageControl.apply {
                        setImageResource(R.drawable.ic_up)
                        setOnClickListener { bottomSheetBehavior.state = STATE_EXPANDED }
                    }
                    STATE_EXPANDED -> imageControl.apply {
                        setImageResource(R.drawable.ic_down)
                        setOnClickListener { bottomSheetBehavior.state = STATE_COLLAPSED }
                    }
                }
            }

        })
        imageControl.setOnClickListener { bottomSheetBehavior.state = STATE_EXPANDED }
        imageStar.apply {
            if (presenter.isStared(image)) {
                setImageResource(R.drawable.ic_stared)
            } else {
                setImageResource(R.drawable.ic_stared_false)
            }
        }
        imageStar.setOnClickListener {
            if (presenter.isStared(image)) {
                presenter.unStarImage(image)
                imageStar.setImageResource(R.drawable.ic_stared_false)
                ToastUtil.show("已取消收藏")
            } else {
                presenter.starImage(image)
                imageStar.setImageResource(R.drawable.ic_stared)
                ToastUtil.show("已收藏")
            }
        }
        presenter.getDetailImage(image.imageId)
    }


    override fun onGetDetailImageSuccess(detailImageInfo: DetailImageInfo) {
        Glide.with(context!!)
                .loadWithProgress(detailImageInfo.url) { progress ->
                    activity?.runOnUiThread {
                        textProgress?.text = "$progress%"
                    }
                }.listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        if (userVisibleHint) ToastUtil.show("图片加载失败 >_<")
                        refreshLayout?.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        loadingLayout?.visibility = View.INVISIBLE
                        imageDownload?.visibility = View.VISIBLE
                        return false
                    }

                }).into(photoView)
        textResolution.text = detailImageInfo.resolution
        detailImageInfo.tags.forEach { tag ->
            chipGroup.addView(Chip(context).apply {
                this.text = tag.name
                this.setOnClickListener {
                    //TODO
                    ToastUtil.show("${tag.id}")
                }
            })
        }
        imageControl.visibility = View.VISIBLE
    }

    override fun onGetDetailImageFail(msg: String) {
        ToastUtil.show(msg)
        refreshLayout?.visibility = View.INVISIBLE
    }

}