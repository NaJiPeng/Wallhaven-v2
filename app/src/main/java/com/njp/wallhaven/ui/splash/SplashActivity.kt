package com.njp.wallhaven.ui.splash

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.njp.wallhaven.R
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

/**
 * 闪屏页
 */
class SplashActivity : BaseActivity<SplashContract.View, SplashPresenter>(), SplashContract.View {

    private var seconds = 5


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setP(SplashPresenter(this))

        btnSkip.setOnClickListener { toMainActivity() }

        Glide.with(this)
                .load(R.drawable.logo)
                .into(ivLogo)


        presenter.getSplashImages()
        presenter.updateSplashImages()
        presenter.startTimer()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }


    override fun onSplashImages(image: SimpleImageInfo) {
        Glide.with(this)
                .load(image.url)
                .apply(RequestOptions().apply {
                    override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    diskCacheStrategy(DiskCacheStrategy.DATA)
                    placeholder(R.drawable.splash_backgroung)
                })
                .into(ivBackground)

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
