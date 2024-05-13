package com.anafthdev.story.foundation.extension

import com.anafthdev.story.ProtoUserPreference
import com.anafthdev.story.data.Language
import com.anafthdev.story.data.model.UserPreference

fun ProtoUserPreference.toUserPreference() = UserPreference(
    language = Language.fromCode(language)!!

)
