package com.anafthdev.story.foundation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object UriUtil {

    fun getUriFromCache(context: Context, displayName: String): Uri {
        val cacheDir = context.cacheDir
        val file = File.createTempFile(displayName, ".jpg", cacheDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val file = File.createTempFile(System.currentTimeMillis().toString(), ".jpg", context.cacheDir)
        val inputStream = context.contentResolver.openInputStream(uri) as InputStream
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return file
    }

}