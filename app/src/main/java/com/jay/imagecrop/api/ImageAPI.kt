package com.jay.imagecrop.api

import com.jay.imagecrop.model.ImageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageAPI {

    @GET("/api/")
    suspend fun getUserList(
        @Query("key") key: String,
        @Query("image_type") imageType: String,
        ): Response<ImageResponse>
}