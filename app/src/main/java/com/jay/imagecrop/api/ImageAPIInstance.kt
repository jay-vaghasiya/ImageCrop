package com.jay.imagecrop.api

import com.jay.imagecrop.utils.NetworkModule
import okhttp3.OkHttpClient

object ImageAPIInstance {

    val api: ImageAPI by lazy {
        NetworkModule.provideRetrofit(OkHttpClient())
            .create(ImageAPI::class.java)
    }
}