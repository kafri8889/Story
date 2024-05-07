package com.anafthdev.story.foundation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object UriUtil {

    fun getUriFromCache(context: Context, displayName: String): Uri {
        val cacheDir = context.cacheDir
        val file = File.createTempFile(displayName, ".jpg", cacheDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
    }

}