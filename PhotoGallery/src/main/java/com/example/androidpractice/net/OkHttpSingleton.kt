package com.example.androidpractice.net

import android.content.Context
import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

class OkHttpSingleton private constructor(context: Context) {
    companion object {
        private var INSTANCE: OkHttpSingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                OkHttpSingleton(context).also { INSTANCE = it }
            }

    }

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .addInterceptor {
                val request = it.request()
                Log.d("网络请求", "请求url : ${request.url}")
                it.proceed(request)
            }
            .cache(Cache(File(context.cacheDir, "cache"), 1024 * 1024 * 20))
            .build()
    }
}