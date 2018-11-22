package com.njp.wallhaven.ui.splash

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.njp.wallhaven.R
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.ui.main.MainActivity
import com.njp.wallhaven.ui.splash.gallerys.*
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

/**
 * 闪屏页
 */
class SplashActivity : BaseActivity<SplashContract.View, SplashPresenter>(), SplashContract.View {

    private var seconds = 30000
    private val fragments = ArrayList<GalleryFragment>(7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setP(SplashPresenter(this))

        btnSkip.setOnClickListener { toMainActivity() }

        val into = Glide.with(this)
                .load(R.drawable.logo)
                .into(ivLogo)

        initFragments()

        presenter.getSplashImages()
        presenter.updateSplashImages()
        presenter.startTimer()
    }

    private fun initFragments() {
        fragments.add(GalleryFragment1())
        fragments.add(GalleryFragment2())
        fragments.add(GalleryFragment3())
        fragments.add(GalleryFragment4())
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }


    override fun onSplashImages(images: MutableList<SimpleImageInfo>) {
        val fragment = fragments[Random().nextInt(fragments.size)].apply { setImages(images) }
        supportFragmentManager.beginTransaction()
                .add(R.id.layout_gallery,fragment)
                .show(fragment)
                .commit()
    }

    override fun onNoSplashImage() {
        Glide.with(this)
                .load(R.drawable.splash_backgroung)
                .into(ivBackground)
    }

    override fun onTimer() {
        if (--seconds >= 0) {
            btnSkip.text = "跳过 $seconds"
        } else {
            toMainActivity()
        }
    }


    private fun toMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}
