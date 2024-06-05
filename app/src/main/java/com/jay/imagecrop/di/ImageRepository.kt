package com.jay.imagecrop.di

import android.os.Build
import androidx.annotation.RequiresExtension
import com.jay.imagecrop.api.ImageAPIInstance
import java.io.IOException
import javax.inject.Singleton

@Singleton
class ImageRepository {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun getUserList(key: String, imageType: String): Any? {

        val response = try {
            ImageAPIInstance.api.getUserList(key, imageType)
        } catch (e: IOException) {
            return e.message
        } catch (e: retrofit2.HttpException) {
            return e.message
        } catch (e: Exception) {
            return e.message
        }
        if (response.isSuccessful && response.body() != null) {
            val userResponse = response.body()

        }
        return response.body() ?: ""
    }

}