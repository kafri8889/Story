package com.anafthdev.story.data.model.body

data class RegisterRequestBody(
    val name: String,
    val email: String,
    val password: String
)