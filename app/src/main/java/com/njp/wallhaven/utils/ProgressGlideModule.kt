package com.njp.wallhaven.utils

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.njp.wallhaven.repositories.network.ProgressInterceptor
import com.njp.wallhaven.repositories.network.NetworkInstance.client
import java.io.InputStream

/**
 * 带进度加载图片
 */
@GlideModule
class OkHttpGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }


}

/**
 * 带进度加载的拓展方法
 */
fun RequestManager.loadWithProgress(url: String, progressListener: (Int) -> Unit): RequestBuilder<Drawable> {
    ProgressInterceptor.addListener(url, progressListener)
    return this.load(url)
}