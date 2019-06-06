package com.njp.wallhaven3.repositories.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 网络进度拦截器
 */
class ProgressInterceptor : Interceptor {

    companion object {
        //监听列表
        private val LISTENER_MAP = HashMap<String, (Int) -> Unit>()

        //获取监听器
        fun getListener(url: String) = LISTENER_MAP[url]

        //注册下载监听
        fun addListener(url: String, progressListener: (Int) -> Unit) = LISTENER_MAP.put(url, progressListener)

        //取消下载监听
        fun removeListener(url: String) = LISTENER_MAP.remove(url)
    }

    override fun intercept(chain: Interceptor.Chain?): Response {
        val request = chain!!.request()
        val response = chain.proceed(request)
        val url = request.url().toString()
        val body = response.body()!!
        return response.newBuilder().body(ProgressResponseBody(url, body)).build()
    }

}
