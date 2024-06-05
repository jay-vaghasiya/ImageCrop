package com.jay.imagecrop.di

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jay.imagecrop.application.ImageCropApp
import com.jay.imagecrop.model.ImageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val _userResponseLiveData = MutableLiveData<ImageResponse?>()
    val _errorMessageLiveData = MutableLiveData<String?>()


    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun userList(key: String, imageType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userResponse = ImageCropApp.imageRepository.getUserList(key, imageType)

            if (userResponse is ImageResponse) {
                _userResponseLiveData.postValue(userResponse)
            } else if (userResponse is String) {
                _errorMessageLiveData.postValue(userResponse)
            } else {
                //Do nothing
            }
        }
    }
}