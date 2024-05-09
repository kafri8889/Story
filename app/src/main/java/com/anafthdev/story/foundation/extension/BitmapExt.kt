package com.anafthdev.story.foundation.extension

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

fun Bitmap.rotate(angle: Float): Bitmap {
    return Bitmap.createBitmap(
        this,
        0,
        0,
        width,
        height,
        Matrix().apply {
            postRotate(angle)
        },
        true
    )
}

fun Bitmap.getRotatedBitmap(stream: InputStream): Bitmap {
    val orientation = ExifInterface(stream).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )

    Timber.i("Bitmap orientation: $orientation")

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotate(270f)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun Bitmap.reduceSize(maxSize: Int, os: OutputStream) {
    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > maxSize)

    compress(Bitmap.CompressFormat.JPEG, compressQuality, os)
}
