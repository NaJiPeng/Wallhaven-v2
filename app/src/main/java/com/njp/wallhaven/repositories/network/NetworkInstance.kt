package com.njp.wallhaven.repositories.network

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object NetworkInstance {

    val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(ProgressInterceptor())
            .build()

    val retrofit: Retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://alpha.wallhaven.cc/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /**
     * 网络数据读取接口
     */
    interface RetrofitService {

        @GET(" ")
        fun getSplashImages(): Observable<ResponseBody>

    }

}