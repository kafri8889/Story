package com.anafthdev.story.data.model.response

import com.anafthdev.story.data.model.LoginResult
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("error")
    val error: Boolean,
    val loginResult: LoginResult,
    @field:SerializedName("message")
    val message: String
)