package com.anafthdev.story.data.model

data class UserCredential(
    val username: String,
    val email: String,
    val userId: String,
    val token: String
) {

    val isValid: Boolean
        get() = token.isNotBlank() &&
                username.isNotBlank() &&
                userId.isNotBlank() &&
                email.isNotBlank()
}
