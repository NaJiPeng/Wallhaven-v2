package com.njp.wallhaven.ui.search.image

import android.Manifest
import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
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
import com.njp.wallhaven.adapter.ImagesAdapter
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator
import kotlinx.android.synthetic.main.activity_image_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
        title = "以图搜图"

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
            recyclerView.smoothScrollToPosition(0)
            animator.start()
        }

        refreshLayout.setOnRefreshListener {
            Glide.with(this)
                    .load(path)
                    .apply(RequestOptions().centerCrop())
                    .into(imageUpload)
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
                rxPermissions.request(Manifest.permission.CAMERA)
                        .subscribe { granted ->
                            if (granted) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                val file = UriUtil.getInstance().getTempFilePath()
                                cameraPath = file.absolutePath
                                val uri = if (Build.VERSION.SDK_INT >= 24)
                                    FileProvider.getUriForFile(
                                            this,
                                            "com.njp.wallhaven.fileprovider",
                                            file
                                    ) else Uri.fromFile(file)
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                                startActivityForResult(intent, CODE_TAKE_PHOTO)
                            }else {
                                ToastUtil.show("未授权 T_T")
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
                    path = UriUtil.getInstance().getImageAbsolutePath(list[0])!!
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

    private fun onChangeColor(color: Pair<String, Int>) {
        appBarLayout.setBackgroundColor(color.second)
        collapsingLayout.setContentScrimColor(color.second)
        collapsingLayout.statusBarScrim = ColorDrawable(color.second)
        fab.colorNormal = color.second
        fab.colorPressed = color.second
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
