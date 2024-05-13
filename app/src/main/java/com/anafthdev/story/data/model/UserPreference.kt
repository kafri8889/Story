package com.anafthdev.story.data.model

import android.os.Parcelable
import com.anafthdev.story.data.Language
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserPreference(
    val language: Language
): Parcelable