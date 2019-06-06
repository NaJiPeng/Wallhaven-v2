package com.njp.wallhaven3.ui.search.text

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import com.jaeger.library.StatusBarUtil
import com.njp.wallhaven3.R
import com.njp.wallhaven3.adapter.CategoriesAdapter
import com.njp.wallhaven3.adapter.ColorDropMenuAdpter
import com.njp.wallhaven3.adapter.ImagesAdapter
import com.njp.wallhaven3.adapter.TextDropMenuAdapter
import com.njp.wallhaven3.base.BaseActivity
import com.njp.wallhaven3.repositories.bean.SimpleImageInfo
import com.njp.wallhaven3.repositories.bean.Tag
import com.njp.wallhaven3.utils.ActivityController
import com.njp.wallhaven3.utils.ColorUtil
import com.njp.wallhaven3.utils.ScrollToEvent
import com.njp.wallhaven3.utils.ToastUtil
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.footer.BallPulseFooter
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.activity_text_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 搜索页面
 */
class TextSearchActivity : BaseActivity<TextSearchContract.View, TextSearchPresenter>(), TextSearchContract.View {

    companion object {
        fun actionStart(context: Context, q: String) {
            val intent = Intent(context, TextSearchActivity::class.java)
            intent.putExtra("q", q)
            context.startActivity(intent)
        }

        fun actionStart(context: Context, tag: Tag) {
            val intent = Intent(context, TextSearchActivity::class.java)
            intent.putExtra("q", "id:${tag.id}")
            intent.putExtra("title", tag.name)
            context.startActivity(intent)
        }
    }

    private var q = ""
    private var ratios = ""
    private var colors = ""
    private var sorting = ""
    private var topRange = ""
    private var categories = "111"
    private val adapter = ImagesAdapter()

    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var footer: BallPulseFooter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityController.getInstance().add(this)
        setContentView(R.layout.activity_text_search)
        setP(TextSearchPresenter(this))

        q = intent.getStringExtra("q") ?: ""
        title = intent.getStringExtra("title") ?: q

        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_back)
        toolBar.setNavigationOnClickListener { onBackPressed() }

        setDropDownMenu()

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.itemAnimator = SlideInUpAnimator()
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
            animator.start()
            recyclerView.smoothScrollToPosition(0)
        }

        refreshLayout.setOnRefreshListener {
            adapter.clear()
            presenter.refreshImages(q, ratios, colors, sorting, topRange, categories)
        }
        refreshLayout.setOnLoadMoreListener { presenter.loadMoreImages(q, ratios, colors, sorting, topRange, categories) }

        onChangeColor(ColorUtil.getInstance().getCurrentColor())

        refreshLayout.autoRefresh()
    }

    private fun setDropDownMenu() {

        val contentView = LayoutInflater.from(this)
                .inflate(R.layout.search_content, dropDownMenu, false)
        recyclerView = contentView.findViewById(R.id.recyclerView)
        refreshLayout = contentView.findViewById(R.id.refreshLayout)
        footer = contentView.findViewById(R.id.footer)

        val tabs = listOf("比例", "种类", "排序", "时间", "颜色")

        val recyclerViewRatio = LayoutInflater.from(this)
                .inflate(R.layout.drop_down_menu, dropDownMenu, false) as RecyclerView
        recyclerViewRatio.layoutManager = GridLayoutManager(this, 5)
        val ratiosList = listOf("", "4x3", "5x4", "16x9", "16x10", "21x9", "32x9", "48x9", "9x16", "10x16")
        recyclerViewRatio.adapter = TextDropMenuAdapter(
                listOf("不限", "4x3", "5x4", "16x9", "16x10", "21x9", "32x9", "48x9", "9x16", "10x16"),
                ColorUtil.getInstance().getCurrentColor().second
        ) { position ->
            val ratio = ratiosList[position]
            if (ratios != ratio) {
                ratios = ratio
                if (refreshLayout.state == RefreshState.Refreshing || refreshLayout.state == RefreshState.RefreshFinish) {
                    presenter.disposeAll()
                    adapter.clear()
                    presenter.refreshImages(q, ratios, colors, sorting, topRange, categories)
                } else {
                    refreshLayout.autoRefresh()
                }
            }
            dropDownMenu.closeMenu()
        }

        val recyclerViewColors = LayoutInflater.from(this)
                .inflate(R.layout.drop_down_menu, dropDownMenu, false) as RecyclerView
        recyclerViewColors.layoutManager = GridLayoutManager(this, 5)
        val colorList = listOf(
                "00000000", "660000", "990000", "cc0000", "cc3333", "ea4c88",
                "993399", "663399", "333399", "0066cc", "0099cc", "66cccc",
                "77cc33", "669900", "336600", "666600", "999900", "cccc33",
                "ffff00", "ffcc33", "ff9900", "ff6600", "cc6633", "996633",
                "663300", "000000", "999999", "cccccc", "ffffff", "424153"
        )
        recyclerViewColors.adapter = ColorDropMenuAdpter(
                colorList,
                ColorUtil.getInstance().getCurrentColor().second
        ) { position ->
            val color = if (position == 0) "" else colorList[position]
            if (colors != color) {
                colors = color
                if (refreshLayout.state == RefreshState.Refreshing || refreshLayout.state == RefreshState.RefreshFinish) {
                    presenter.disposeAll()
                    adapter.clear()
                    presenter.refreshImages(q, ratios, colors, sorting, topRange, categories)
                } else {
                    refreshLayout.autoRefresh()
                }
            }
            dropDownMenu.closeMenu()

        }

        val recyclerViewSorting = LayoutInflater.from(this)
                .inflate(R.layout.drop_down_menu, dropDownMenu, false) as RecyclerView
        recyclerViewSorting.layoutManager = GridLayoutManager(this, 5)
        val sortingList = listOf("relevance", "random", "date_added", "views", "favorites", "toplist")
        val sortingTitles = listOf("相关度", "随机", "最新", "点击量", "收藏量", "热门")
        recyclerViewSorting.adapter = TextDropMenuAdapter(
                sortingTitles,
                ColorUtil.getInstance().getCurrentColor().second
        ) { position ->
            val sort = sortingList[position]
            if (sorting != sort) {
                sorting = sort
                if (refreshLayout.state == RefreshState.Refreshing || refreshLayout.state == RefreshState.RefreshFinish) {
                    presenter.disposeAll()
                    adapter.clear()
                    presenter.refreshImages(q, ratios, colors, sorting, topRange, categories)
                } else {
                    refreshLayout.autoRefresh()
                }
            }
            dropDownMenu.closeMenu()
        }

        val recyclerViewRange = LayoutInflater.from(this)
                .inflate(R.layout.drop_down_menu, dropDownMenu, false) as RecyclerView
        recyclerViewRange.layoutManager = GridLayoutManager(this, 5)
        val topRangeList = listOf("1d", "3d", "1w", "1M", "3M", "6M", "1y")
        val topRangeTitles = listOf("一天", "三天", "一周", "一个月", "三个月", "六个月", "一年")
        recyclerViewRange.adapter = TextDropMenuAdapter(
                topRangeTitles,
                ColorUtil.getInstance().getCurrentColor().second,
                3
        ) { position ->
            val range = topRangeList[position]
            if (topRange != range) {
                topRange = range
                if (sorting == "toplist") {
                    if (refreshLayout.state == RefreshState.Refreshing || refreshLayout.state == RefreshState.RefreshFinish) {
                        presenter.disposeAll()
                        adapter.clear()
                        presenter.refreshImages(q, ratios, colors, sorting, topRange, categories)
                    } else {
                        refreshLayout.autoRefresh()
                    }
                }
            }
            dropDownMenu.closeMenu()
        }

        val recyclerViewCategories = LayoutInflater.from(this)
                .inflate(R.layout.drop_down_menu, dropDownMenu, false) as RecyclerView
        recyclerViewCategories.layoutManager = GridLayoutManager(this, 5)
        recyclerViewCategories.adapter = CategoriesAdapter(
                listOf("普通", "动漫", "人物"), ColorUtil.getInstance().getCurrentColor().second
        ) {
            if (categories != it) {
                categories = it
                if (refreshLayout.state == RefreshState.Refreshing || refreshLayout.state == RefreshState.RefreshFinish) {
                    presenter.disposeAll()
                    adapter.clear()
                    presenter.refreshImages(q, ratios, colors, sorting, topRange, categories)
                } else {
                    refreshLayout.autoRefresh()
                }
            }
            dropDownMenu.closeMenu()
        }

        dropDownMenu.setDropDownMenu(
                tabs,
                listOf(recyclerViewRatio, recyclerViewCategories, recyclerViewSorting, recyclerViewRange, recyclerViewColors),
                contentView
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val item = menu?.findItem(R.id.search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "搜索(建议English)"
        searchView.setOnSearchClickListener {
            searchView.setQuery(toolBar.title, false)
        }
        searchView.setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(p0: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        searchView.onActionViewCollapsed()
                        q = p0 ?: ""
                        title = q
                        presenter.saveHistory(q)
                        adapter.clear()
                        refreshLayout.autoRefresh()
                        return true
                    }

                }
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        ActivityController.getInstance().remove(this)
    }

    override fun onRefreshImages(images: List<SimpleImageInfo>) {
        adapter.setData(images)
        refreshLayout.finishRefresh()
        refreshLayout.setEnableLoadMore(true)
    }

    override fun onRefreshImagesFail(msg: String) {
        ToastUtil.show(msg)
        refreshLayout.finishRefresh()
        refreshLayout.setEnableLoadMore(false)
    }

    override fun onNoImages() {
        ToastUtil.show("什么都没有找到 ~_~")
        refreshLayout.finishRefresh()
        refreshLayout.setEnableLoadMore(false)
    }

    override fun onLoadMoreImages(images: List<SimpleImageInfo>) {
        adapter.addData(images)
        refreshLayout.finishLoadMore()
    }

    override fun onLoadMoreImagesFail(msg: String) {
        ToastUtil.show(msg)
        refreshLayout.finishLoadMore()
    }

    override fun onNoMoreImages() {
        ToastUtil.show("没有更多了 >_<")
        refreshLayout.finishLoadMore()
        refreshLayout.setEnableLoadMore(false)
    }

    private fun onChangeColor(color: Pair<String, Int>) {
        StatusBarUtil.setColorNoTranslucent(this, color.second)
        toolBar.setBackgroundColor(color.second)
        fab.colorNormal = color.second
        fab.colorPressed = color.second
        footer.setAnimatingColor(color.second)
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
