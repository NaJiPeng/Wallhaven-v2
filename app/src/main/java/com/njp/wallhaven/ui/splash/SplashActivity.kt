package com.njp.wallhaven.ui.splash

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.njp.wallhaven.R
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.repositories.bean.SimpleImageInfo
import com.njp.wallhaven.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * 闪屏页
 */
class SplashActivity : BaseActivity<SplashContract.View, SplashPresenter>(), SplashContract.View {

    private var seconds = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setP(SplashPresenter(this))

        btnSkip.setOnClickListener { toMainActivity() }

        Glide.with(this)
                .load(R.drawable.logo)
                .into(ivLogo)

        presenter.getSplashImage()
        presenter.updateSplashImages()
        presenter.startTimer()

    }

    override fun onSplashImage(imageInfo: SimpleImageInfo?) {
        Glide.with(this)
                .load(imageInfo?.url ?: R.drawable.splash_backgroung)
                .apply(RequestOptions().placeholder(R.drawable.splash_backgroung))
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
