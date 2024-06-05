package com.jay.imagecrop.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog

fun Activity.showConfirmationDialog(
    titleResId: String?,
    messageResId: String?,
    positiveButtonLabel: String = "OK",
    onPositiveButtonClick: () -> Unit = {}
) {

    AlertDialog.Builder(this)
        .setMessage(messageResId)
        .setTitle(titleResId)
        .setCancelable(true)
        .setPositiveButton(positiveButtonLabel) { dialog, _ ->
            dialog.dismiss()
            onPositiveButtonClick()
        }
        .create()
        .show()
}