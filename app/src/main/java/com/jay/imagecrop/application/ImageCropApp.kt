package com.jay.imagecrop.application

import android.app.Application
import com.jay.imagecrop.di.ImageRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ImageCropApp:Application() {
companion object{

    lateinit var imageRepository: ImageRepository
    private set

}
    override fun onCreate() {
        super.onCreate()
        imageRepository = ImageRepository()
    }
}