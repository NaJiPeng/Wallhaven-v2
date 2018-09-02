package com.njp.wallhaven.ui.search.image

import android.Manifest
import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.njp.wallhaven.R
import com.jaeger.library.StatusBarUtil
import com.njp.wallhaven.adapter.ImagesAdapter
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.utils.ColorUtil
import com.njp.wallhaven.utils.Glide4Engine
import com.njp.wallhaven.utils.ToastUtil
import com.njp.wallhaven.utils.UriToPathUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator
import kotlinx.android.synthetic.main.activity_image_search.*
import java.io.File


class ImageSearchActivity : BaseActivity<ImageSearchContract.View, ImageSearchPresenter>(), ImageSearchContract.View {

    companion object {
        private const val CODE_CHOOSE = 10001
        private const val CODE_TAKE_PHOTO = 10002
        fun actionStart(context: Context, path: String) {
            val intent = Intent(context, ImageSearchActivity::class.java)
            intent.putExtra("path", path)
            context.startActivity(intent)
        }
    }

    private lateinit var rxPermissions: RxPermissions
    private val adapter = ImagesAdapter()
    private var path = ""
    private var cameraPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_search)
        setP(ImageSearchPresenter(this))
        rxPermissions = RxPermissions(this)

        path = intent.getStringExtra("path")

        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { onBackPressed() }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.itemAnimator = FlipInTopXAnimator()
        recyclerView.adapter = SlideInBottomAnimationAdapter(adapter)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) fab.hide(true) else fab.show(true)
            }
        })

        val animator = AnimatorInflater.loadAnimator(this, R.animator.animator_fab)
        animator.setTarget(fab)
        fab.setOnClickListener {
            scrollView.smoothScrollTo(0, 0)
            animator.start()
        }

        refreshLayout.setOnRefreshListener {
            Glide.with(this)
                    .load(path)
                    .apply(RequestOptions().centerCrop())
                    .into(imageYour)
            adapter.clear()
            presenter.getImages(this, path)
        }

        onChangeColor(ColorUtil.getInstance().getCurrentColor())

        refreshLayout.autoRefresh()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when (item?.itemId) {
            R.id.camera -> {
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe { granted ->
                            if (granted) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                val dir = File("${Environment.getExternalStorageDirectory().path}/Wallhaven/temp")
                                if (!dir.exists()) {
                                    dir.mkdirs()
                                } else if (dir.listFiles().size >= 5) {
                                    dir.listFiles().forEach { it.delete() }
                                }
                                val file = File(dir, "temp-${System.currentTimeMillis()}.jpg")
                                cameraPath = file.absolutePath
                                val uri = FileProvider.getUriForFile(
                                        this,
                                        "com.njp.wallhaven.fileprovider",
                                        file
                                )
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                                startActivityForResult(intent, CODE_TAKE_PHOTO)
                            }
                        }
            }
            R.id.scan -> {
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe { granted ->
                            if (granted) {
                                Matisse.from(this)
                                        .choose(MimeType.ofImage())
                                        .countable(false)
                                        .maxSelectable(1)
                                        .theme(R.style.Matisse_Dracula)
                                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                        .imageEngine(Glide4Engine())
                                        .forResult(CODE_CHOOSE)
                            }
                        }
            }
        }
        return true
    }

    private fun onChangeColor(color: Pair<String, Int>) {
        StatusBarUtil.setColorNoTranslucent(this, color.second)
        toolBar.setBackgroundColor(color.second)
        fab.colorNormal = color.second
        fab.colorPressed = color.second
    }

    override fun onGetImages(images: List<SimpleImageInfo>) {
        adapter.setData(images)
        refreshLayout.finishRefresh()
    }

    override fun onGetImagesFail(msg: String) {
        ToastUtil.show(msg)
        refreshLayout.finishRefresh()
    }

    override fun onGetNoImages() {
        ToastUtil.show("没有找到任何图片")
        refreshLayout.finishRefresh()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CODE_CHOOSE -> {
                if (resultCode == RESULT_OK) {
                    val list = Matisse.obtainResult(data)
                    path = UriToPathUtil.getInstance().getImageAbsolutePath(list[0])!!
                    refreshLayout.autoRefresh()
                }
            }
            CODE_TAKE_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    path = cameraPath
                    refreshLayout.autoRefresh()
                }
            }
        }
    }

}
