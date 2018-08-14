package com.njp.wallhaven.ui.splash

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.bumptech.glide.Glide
import com.njp.wallhaven.R
import com.njp.wallhaven.base.BaseActivity
import com.njp.wallhaven.bean.SimpleImageInfo
import com.njp.wallhaven.databinding.ActivitySplashBinding
import com.njp.wallhaven.ui.main.MainActivity

/**
 * 闪屏页
 */
class SplashActivity : BaseActivity<SplashContract.View, SplashPresenter>(), SplashContract.View {

    private lateinit var binding: ActivitySplashBinding
    private var seconds = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        setP(SplashPresenter(this))

        binding.btnSkip.setOnClickListener { toMainActivity() }

        Glide.with(this)
                .load(R.drawable.logo)
                .into(binding.ivLogo)

        presenter.getSplashImage()
        presenter.updateSplashImages()
        presenter.startTimer()

    }

    override fun onSplashImage(imageInfo: SimpleImageInfo?) {
        Glide.with(this)
                .load(imageInfo?.url ?: R.drawable.splash_backgroung)
                .into(binding.ivBackground)
    }

    override fun onTimer() {
        if (--seconds >= 0) {
            binding.btnSkip.text = "跳过 $seconds"
        } else {
            toMainActivity()
        }
    }


    private fun toMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}
