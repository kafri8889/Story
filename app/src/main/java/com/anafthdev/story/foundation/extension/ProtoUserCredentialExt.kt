package com.anafthdev.story.foundation.extension

import com.anafthdev.story.ProtoUserCredential
import com.anafthdev.story.data.model.UserCredential

fun ProtoUserCredential.toUserCredential(): UserCredential {
    return UserCredential(
        username = username,
        email = email,
        userId = userId,
        token = token
    )
}
