package com.njp.wallhaven3.ui.detail

import android.Manifest
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
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
import com.njp.wallhaven3.R
import com.njp.wallhaven3.base.BaseFragment
import com.njp.wallhaven3.repositories.bean.DetailImageInfo
import com.njp.wallhaven3.repositories.bean.SimpleImageInfo

import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_detail.*
import java.io.File
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.njp.wallhaven3.repositories.network.ProgressInterceptor
import com.njp.wallhaven3.ui.tag.TagActivity
import com.njp.wallhaven3.utils.*
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.FileOutputStream


class DetailFragment : BaseFragment<DetailContract.View, DetailPresenter>(), DetailContract.View {

    companion object {
        fun create(image: SimpleImageInfo, size: Int, position: Int) = DetailFragment().apply {
            this.image = image
            this.size = size
            this.position = position
            setP(DetailPresenter(this))
        }
    }

    private lateinit var image: SimpleImageInfo
    private var size = 0
    private var position = 0
    private var detailImageInfo: DetailImageInfo? = null
    private var bitmap: Bitmap? = null
    private lateinit var rxPermissions: RxPermissions
    private lateinit var loadingDialog: Dialog
    private lateinit var shareDialog: Dialog
    private var tempPath = ""

    private val shareListener = View.OnClickListener {
        shareDialog.dismiss()
        loadingDialog.show()
        Thread {
            val tempFile = UriUtil.getInstance().getTempFilePath()
            tempPath = tempFile.absolutePath
            saveImageToDisk(tempFile)
            activity?.let { act ->
                act.runOnUiThread {
                    loadingDialog.dismiss()
                    when (it.id) {
                        R.id.imageQQ -> TencentUtil.getInstance().shareImageToQQ(act, tempPath)
                        R.id.imageQzone -> TencentUtil.getInstance().shareImageToQzone(act, tempPath)
                        R.id.imageWechat -> bitmap?.let { bmp ->
                            TencentUtil.getInstance().shareImageToWechat(bmp)
                        }
                        R.id.imageCircleOfFriends -> bitmap?.let { bmp ->
                            TencentUtil.getInstance().shareImageToCircleOfFriend(bmp)
                        }
                    }
                }
            }
        }.start()
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailImageInfo = CommonDataHolder.getDetailData(position)
        rxPermissions = RxPermissions(this)
        (textIndicate as TextView).text = "${position + 1}/$size"
        photoView.maximumScale *= 2
        Glide.with(context!!).load(image.url).into(photoView)

        initDialog()

        initChipGroup()

        initImageEvent()

        photoView.setOnClickListener { activity?.finish() }

        if (detailImageInfo != null) {
            onGetDetailImageSuccess(detailImageInfo!!)
        } else {
            presenter.getDetailImage(image.id)
        }

        EventBus.getDefault().register(this)
    }

    private fun initDialog() {
        loadingDialog = Dialog(context, R.style.dialog)
        val dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_loading, null)
        loadingDialog.setContentView(dialogView)
        loadingDialog.setCancelable(false)

        shareDialog = Dialog(context, R.style.dialog)
        val shareView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_share, null)
        shareView.findViewById<ImageView>(R.id.imageQQ).setOnClickListener(shareListener)
        shareView.findViewById<ImageView>(R.id.imageWechat).setOnClickListener(shareListener)
        shareView.findViewById<ImageView>(R.id.imageQzone).setOnClickListener(shareListener)
        shareView.findViewById<ImageView>(R.id.imageCircleOfFriends).setOnClickListener(shareListener)
        shareDialog.setContentView(shareView)
    }

    private fun initImageEvent() {
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
                            loadingDialog.show()
                            Thread {
                                val file = UriUtil.getInstance().getDownloadFilePath(image.id)
                                saveImageToDisk(file)
                                activity?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
                                activity?.runOnUiThread {
                                    loadingDialog.dismiss()
                                    ToastUtil.show("已保存至${file.absoluteFile}")
                                }
                            }.start()
                        } else {
                            ToastUtil.show("未授权 T_T")
                        }
                    }
        }

        imageShare.setOnClickListener { _ ->
            shareDialog.show()
        }

        imageCrop.setOnClickListener { _ ->
            activity?.let {
                loadingDialog.show()
                val file = UriUtil.getInstance().getTempFilePath()
                val tempFile = UriUtil.getInstance().getTempFilePath()
                tempPath = tempFile.absolutePath
                Thread {
                    saveImageToDisk(file)
                    it.runOnUiThread {
                        UCrop
                                .of(
                                        Uri.fromFile(file),
                                        Uri.fromFile(tempFile)
                                )
                                .withOptions(UCrop.Options().apply {
                                    setCompressionQuality(100)

                                    val color = ColorUtil.getInstance().getCurrentColor()
                                    setStatusBarColor(color.second)
                                    setToolbarColor(color.second)
                                    setLogoColor(color.second)
                                    setActiveWidgetColor(color.second)

                                    setAspectRatioOptions(0,
                                            AspectRatio("1:1", 1f, 1f),
                                            AspectRatio("3:4", 3f, 4f),
                                            AspectRatio("2:3", 2f, 3f),
                                            AspectRatio("10:16", 10f, 16f),
                                            AspectRatio("9:16", 9f, 16f),
                                            AspectRatio("9:18", 9f, 18f)
                                    )
                                    setFreeStyleCropEnabled(true)
                                })
                                .start(it)
                        loadingDialog.dismiss()
                    }
                }.start()
            }
        }

        imageScreen.setOnClickListener { _ ->
            activity?.let {
                loadingDialog.show()
                Thread {
                    WallpaperManager.getInstance(it).setBitmap(bitmap)
                    it.runOnUiThread {
                        loadingDialog.dismiss()
                        ToastUtil.show("设置壁纸成功")
                    }
                }.start()
            }

        }
    }

    private fun initChipGroup() {
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
    }

    private fun saveImageToDisk(file: File) {
        val output = FileOutputStream(file)
        bitmap?.let { bitmap ->
            output.use { target ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, target)
            }
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
        loadImage(detailImageInfo.url)
        (textResolution as TextView).text = detailImageInfo.resolution
        detailImageInfo.tags.forEach { tag ->
            chipGroup.addView(Chip(context).apply {
                this.text = tag.name
                this.setOnClickListener {
                    TagActivity.actionStart(context, tag)
                }
            })
        }
        imageControl.visibility = View.VISIBLE
    }


    private fun loadImage(data: Any) {
        Glide.with(this)
                .asBitmap()
                .load(data)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        if (userVisibleHint) ToastUtil.show("图片加载失败 >_<")
                        Log.e("wwww", "onLoadFailed", e)
                        loadingLayout?.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        bitmap = resource
                        loadingLayout?.visibility = View.INVISIBLE
                        imageDownload?.visibility = View.VISIBLE
                        imageScreen?.visibility = View.VISIBLE
                        imageShare?.visibility = View.VISIBLE
                        imageCrop?.visibility = View.VISIBLE
                        return false
                    }
                }).into(photoView)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun cropImageResult(uri: Pair<Int, Uri>) {
        if (position == uri.first) {
            loadImage(uri.second)
        }
    }

    override fun onGetDetailImageFail(msg: String) {
        ToastUtil.show(msg)
        loadingLayout?.visibility = View.INVISIBLE
    }

}