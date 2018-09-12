package com.njp.wallhaven.ui.search.start

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.chip.Chip
import android.support.v4.content.FileProvider
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.view.View
import android.view.Menu
import android.widget.LinearLayout
import com.jaeger.library.StatusBarUtil
import com.njp.wallhaven.R
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.History
import com.njp.wallhaven.repositories.bean.Tag
import com.njp.wallhaven.ui.search.image.ImageSearchActivity
import com.njp.wallhaven.ui.search.text.TextSearchActivity
import com.njp.wallhaven.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.activity_start_search.*
import java.io.File

class StartSearchActivity : BaseActivity<StartSearchContract.View, StartSearchPresenter>(), StartSearchContract.View {

    companion object {
        private const val CODE_CHOOSE = 10001
        private const val CODE_TAKE_PHOTO = 10002
    }

    private lateinit var rxPermissions: RxPermissions
    private var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_search)
        setP(StartSearchPresenter(this))

        rxPermissions = RxPermissions(this)

        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { onBackPressed() }

        layoutImageSearch.setOnClickListener {
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

        layoutCameraSearch.setOnClickListener { _ ->
            rxPermissions.request(Manifest.permission.CAMERA)
                    .subscribe { granted ->
                        if (granted) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            val file = UriUtil.getInstance().getTempFilePath()
                            path = file.absolutePath
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                                    this,
                                    "com.njp.wallhaven.fileprovider",
                                    file
                            ))
                            startActivityForResult(intent, CODE_TAKE_PHOTO)
                        }else {
                            ToastUtil.show("未授权 T_T")
                        }
                    }
        }

        textClearHistory.setOnClickListener {
            AlertDialog.Builder(this)
                    .setMessage("是否清除搜索记录？")
                    .setPositiveButton("确定") { _, _ ->
                        presenter.clearHistory()
                        chipGroupHistory.removeAllViews()
                        layoutHistory.visibility = View.INVISIBLE
                        ToastUtil.show("已清除搜索记录")
                    }.setNegativeButton("取消") { p0, _ ->
                        p0?.dismiss()
                    }.show()
        }

        onChangeColor(ColorUtil.getInstance().getCurrentColor())

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val item = menu?.findItem(R.id.search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "搜索(建议English)"
        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(p0: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        p0?.let {
                            presenter.saveHistory(it)
                            TextSearchActivity.actionStart(this@StartSearchActivity, it)
                        }
                        return true
                    }

                }
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onGetStaredTags(tags: List<Tag>) {
        if (tags.isEmpty()) {
            layoutTags.visibility = View.INVISIBLE
            return
        }
        layoutTags.visibility = View.VISIBLE
        chipGroupTags.removeAllViews()
        tags.forEach { tag ->
            chipGroupTags.addView(
                    Chip(this).apply {
                        text = tag.name
                        setCloseIconResource(R.drawable.ic_delete_mini)
                        setOnClickListener {
                            TextSearchActivity.actionStart(this@StartSearchActivity, tag)
                        }
                        setOnLongClickListener {
                            isCloseIconEnabled = !isCloseIconEnabled
                            return@setOnLongClickListener true
                        }
                        setOnCloseIconClickListener {
                            presenter.deleteTag(tag)
                            chipGroupTags.removeView(this)
                            ToastUtil.show("已删除标签")
                        }
                    }
            )
        }

        chipGroupTags.post {
            val itemHeight = chipGroupTags.getChildAt(0).height
            val standardHeight = 2 * itemHeight + dp2px(this, 15)
            val parentHeight = chipGroupTags.height
            if (parentHeight > standardHeight) {
                scrollViewTags.layoutParams.height = standardHeight
                textExpansionTags.visibility = View.VISIBLE
                textExpansionTags.setOnClickListener {
                    when (textExpansionTags.text) {
                        "展开" -> {
                            scrollViewTags.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                            textExpansionTags.text = "收起"
                        }
                        "收起" -> {
                            scrollViewTags.layoutParams.height = standardHeight
                            textExpansionTags.text = "展开"
                        }
                    }
                }
            }
        }

    }

    override fun onGetSearchHistory(historyList: List<History>) {
        if (historyList.isEmpty()) {
            layoutHistory.visibility = View.INVISIBLE
            return
        }
        layoutHistory.visibility = View.VISIBLE
        chipGroupHistory.removeAllViews()
        historyList.forEach { history ->
            chipGroupHistory.addView(
                    Chip(this).apply {
                        text = history.string
                        setCloseIconResource(R.drawable.ic_delete_mini)
                        setOnClickListener {
                            presenter.saveHistory(history.string)
                            TextSearchActivity.actionStart(this@StartSearchActivity, history.string)
                        }
                        setOnLongClickListener {
                            isCloseIconEnabled = !isCloseIconEnabled
                            return@setOnLongClickListener true
                        }
                        setOnCloseIconClickListener {
                            presenter.deleteHistory(history)
                            chipGroupHistory.removeView(this)
                            ToastUtil.show("已删除记录")
                        }
                    }
            )
        }

        chipGroupHistory.post {
            val itemHeight = chipGroupHistory.getChildAt(0).height
            val standardHeight = 2 * itemHeight + dp2px(this, 15)
            val parentHeight = chipGroupHistory.height
            if (parentHeight > standardHeight) {
                scrollViewHistory.layoutParams.height = standardHeight
                textExpansionHistory.visibility = View.VISIBLE
                textExpansionHistory.setOnClickListener {
                    when (textExpansionHistory.text) {
                        "展开" -> {
                            scrollViewHistory.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                            textExpansionHistory.text = "收起"
                        }
                        "收起" -> {
                            scrollViewHistory.layoutParams.height = standardHeight
                            textExpansionHistory.text = "展开"
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.getStaredTags()
        presenter.getSearchHistory()
    }

    private fun onChangeColor(color: Pair<String, Int>) {
        StatusBarUtil.setColorNoTranslucent(this, color.second)
        toolBar.setBackgroundColor(color.second)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CODE_CHOOSE -> {
                if (resultCode == RESULT_OK) {
                    val list = Matisse.obtainResult(data)
                    ImageSearchActivity.actionStart(
                            this,
                            UriUtil.getInstance().getImageAbsolutePath(list[0])!!
                    )
                }
            }
            CODE_TAKE_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    ImageSearchActivity.actionStart(this, path)
                }
            }
        }

    }

}
