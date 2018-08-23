package com.njp.wallhaven.ui.detail

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.design.chip.Chip
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED
import android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED
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

import com.njp.wallhaven.utils.ToastUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_detail.*
import java.io.File
import android.graphics.Bitmap
import android.net.Uri
import com.njp.wallhaven.repositories.network.ProgressInterceptor
import com.njp.wallhaven.utils.CommonDataHolder
import java.io.FileOutputStream


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
    private var detailImageInfo: DetailImageInfo? = null
    private var bitmap: Bitmap? = null
    private lateinit var rxPermissions: RxPermissions


    override fun createView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailImageInfo = CommonDataHolder.getDetailData(position)
        rxPermissions = RxPermissions(this)
        textIndicate.text = "$position/$size"
        photoView.maximumScale *= 2
        Glide.with(context!!).load(image.url).into(photoView)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                imageControl.setImageResource(if (p1 > 0.5f) R.drawable.ic_down else R.drawable.ic_up)
            }

            override fun onStateChanged(p0: View, p1: Int) {
                when (p1) {
                    STATE_COLLAPSED -> imageControl.apply {
                        setImageResource(R.drawable.ic_up)
                        setOnClickListener { bottomSheetBehavior.state = STATE_EXPANDED }
                        photoView.setOnClickListener { activity?.finish() }
                    }
                    STATE_EXPANDED -> imageControl.apply {
                        setImageResource(R.drawable.ic_down)
                        setOnClickListener { bottomSheetBehavior.state = STATE_COLLAPSED }
                        photoView.setOnClickListener { bottomSheetBehavior.state = STATE_COLLAPSED }
                    }
                }
            }

        })
        imageControl.setOnClickListener { bottomSheetBehavior.state = STATE_EXPANDED }
        photoView.setOnClickListener { activity?.finish() }
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
        imageDownload.setOnClickListener {
            rxPermissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe { granted ->
                        if (granted) {
                            Thread {
                                val path = File("${Environment.getExternalStorageDirectory().path}/Wallhaven")
                                if (!path.exists()) {
                                    path.mkdirs()
                                }
                                val target = File(path, "wallhaven-${image.id}.png")
                                val output = FileOutputStream(target)
                                bitmap?.let { bitmap ->
                                    output.use { target ->
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, target)
                                    }
                                }
                                activity?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(target)))
                                activity?.runOnUiThread {
                                    ToastUtil.show("已保存至${target.absoluteFile}")
                                }
                            }.start()
                        } else {
                            ToastUtil.show("未授权 T_T")
                        }
                    }
        }
        if (detailImageInfo != null) {
            onGetDetailImageSuccess(detailImageInfo!!)
        } else {
            presenter.getDetailImage(image.id)
        }
    }


    override fun onGetDetailImageSuccess(detailImageInfo: DetailImageInfo) {
        if (this.detailImageInfo == null) {
            this.detailImageInfo = detailImageInfo
            CommonDataHolder.setDetailData(detailImageInfo, position)
        }
        ProgressInterceptor.addListener(detailImageInfo.url) { progress ->
            activity?.runOnUiThread { textProgress?.text = "$progress%" }
        }
        Glide.with(this)
                .asBitmap()
                .load(detailImageInfo.url)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        if (userVisibleHint) ToastUtil.show("图片加载失败 >_<")
                        loadingLayout?.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        bitmap = resource
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
        loadingLayout?.visibility = View.INVISIBLE
    }

}