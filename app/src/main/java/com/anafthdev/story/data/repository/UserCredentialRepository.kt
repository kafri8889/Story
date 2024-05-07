package com.anafthdev.story.data.repository

import com.anafthdev.story.data.model.UserCredential
import kotlinx.coroutines.flow.Flow

interface UserCredentialRepository {

    val userCredential: Flow<UserCredential>

    suspend fun setUsername(username: String)

    suspend fun setEmail(email: String)

    suspend fun setUserId(userId: String)

    suspend fun setToken(token: String)

    suspend fun setAll(userCredential: UserCredential)

    suspend fun clear()

}