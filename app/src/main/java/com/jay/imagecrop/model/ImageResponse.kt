package com.jay.imagecrop.model

data class ImageResponse(
    val hits: List<Hit>,
    val total: Int,
    val totalHits: Int
)