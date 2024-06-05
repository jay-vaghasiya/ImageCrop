package com.jay.imagecrop.ui

import ImageAdapter
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager

import com.jay.imagecrop.R
import com.jay.imagecrop.databinding.ActivityMainBinding
import com.jay.imagecrop.di.ImageViewModel
import com.jay.imagecrop.utils.showConfirmationDialog
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(), ImageAdapter.OnImageClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ImageViewModel
    private lateinit var adapter: ImageAdapter
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
            return
        }
        getImageList()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onResume() {
        super.onResume()

        getSearch()

    }

    private fun getSearch() {

        binding.svImage.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { adapter.filter(it) }
                return true
            }
        })
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun getImageList() {
        viewModel.userList(key = KEY, imageType = IMAGE_TYPE)
        viewModel._userResponseLiveData.observe(this) { userResponse ->
            if (userResponse != null) {
                Log.d("token", userResponse.toString())
                adapter = ImageAdapter(userResponse.hits,this)
                binding.rvImage.adapter = adapter
                binding.rvImage.layoutManager =
                    GridLayoutManager(
                        this, 2, GridLayoutManager.VERTICAL, false,
                    )
            }
        }
        viewModel._errorMessageLiveData.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showConfirmationDialog(getString(R.string.error), errorMessage)
            }
        }
    }

    override fun onImageClicked(imageUri: Uri) {
        launchCropActivity(imageUri)
    }

    private fun launchCropActivity(imageUri: Uri) {
        // Create a temporary destination URI for the cropped image
        val tempFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cropped_image.jpg")
        val destinationUri = FileProvider.getUriForFile(this, packageName + ".fileprovider", tempFile)

        // Configure UCrop builder
        val uCrop = UCrop.of(imageUri, destinationUri)
            .withAspectRatio(16F, 9F)
            .withMaxResultSize(400, 400)
            .withOptions(getCropOptions())

        // Start the UCrop activity
        uCrop.start(this)
    }
    private fun getCropOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setCompressionQuality(90)
        return options
    }
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                val resultUri: Uri? = UCrop.getOutput(data!!)
                resultUri?.let { uri ->
                    // Save the cropped image to the gallery
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "Cropped Image")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    if (imageUri != null) {
                        val outputStream = contentResolver.openOutputStream(imageUri)
                        if (outputStream != null) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                        }
                        outputStream?.close()
                        showToast("Image saved to gallery")
                    } else {
                        showToast("Failed to save image to gallery")
                    }
                }
            } else if (resultCode == 96) {
                showToast("Crop operation cancelled or failed")
            } else if (resultCode == RESULT_CANCELED) {
                showToast("Result Canceled")
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val KEY = "43588339-ee7ee4b13adebd4afe09275e7"
        const val IMAGE_TYPE = "photo"
    }
}