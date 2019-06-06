package com.njp.wallhaven3.repositories.network

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object NetworkInstance {

    val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(ProgressInterceptor())
            .build()

    val retrofit: Retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://wallhaven.cc/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /**
     * 网络数据读取接口
     */
    interface RetrofitService {

        @GET("{path}")
        fun getImages(
                @Path("path") path: String,
                @Query("page") page: Int
        ): Observable<ResponseBody>

        @GET("w/{id}")
        fun getDetailImage(
                @Path("id") id: String
        ): Observable<ResponseBody>

        @GET("tag/{id}")
        fun getTagImageInfo(
                @Path("id") id: String
        ): Observable<ResponseBody>

        @GET("search")
        fun searchByText(
                @Query("q") q: String,
                @Query("ratios") ratios: String,
                @Query("colors") colors: String,
                @Query("sorting") sorting: String,
                @Query("topRange") topRange: String,
                @Query("categories") categories: String,
                @Query("page") page: Int

        ): Observable<ResponseBody>

        @Multipart
        @GET("search")
        fun searchByImage(
                @Part image: MultipartBody.Part
        ): Observable<ResponseBody>

    }

}