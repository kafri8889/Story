package com.anafthdev.story.foundation.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import timber.log.Timber

object PermissionUtil {

    /**
     * Check whether the app has the required permissions.
     *
     * return `true` if all [permissions] are granted.
     */
    fun checkPermissionGranted(context: Context, vararg permissions: String): Boolean {
        return permissions.all { permission ->
            (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED).also {
                Timber.d("checkPermissionGranted: $permission | $it")
            }
        }
    }

}