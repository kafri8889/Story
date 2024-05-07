package com.anafthdev.story.foundation.extension

import android.graphics.Bitmap
import android.graphics.Matrix


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
