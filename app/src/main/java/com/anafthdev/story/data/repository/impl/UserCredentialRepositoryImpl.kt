package com.anafthdev.story.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.anafthdev.story.ProtoUserCredential
import com.anafthdev.story.data.model.UserCredential
import com.anafthdev.story.data.repository.UserCredentialRepository
import com.anafthdev.story.data.serializer.UserCredentialSerializer
import com.anafthdev.story.foundation.extension.toUserCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserCredentialRepositoryImpl @Inject constructor(
    private val userCredentialDatastore: DataStore<ProtoUserCredential>
): UserCredentialRepository {

    override val userCredential: Flow<UserCredential>
        get() = userCredentialDatastore.data.map { it.toUserCredential() }

    override suspend fun setUsername(username: String) {
        userCredentialDatastore.updateData {
            it.copy(
                username = username
            )
        }
    }

    override suspend fun setEmail(email: String) {
        userCredentialDatastore.updateData {
            it.copy(
                email = email
            )
        }
    }

    override suspend fun setUserId(userId: String) {
        userCredentialDatastore.updateData {
            it.copy(
                userId = userId
            )
        }
    }

    override suspend fun setToken(token: String) {
        userCredentialDatastore.updateData {
            it.copy(
                token = token
            )
        }
    }

    override suspend fun setAll(userCredential: UserCredential) {
        userCredentialDatastore.updateData {
            it.copy(
                userId = userCredential.userId,
                username = userCredential.username,
                email = userCredential.email,
                token = userCredential.token
            )
        }
    }

    override suspend fun clear() {
        userCredentialDatastore.updateData {
            it.copy(
                userId = "",
                username = "",
                email = "",
                token = ""
            )
        }
    }

    companion object {
        const val DATASTORE_NAME = "user_credential"

        val corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { UserCredentialSerializer.defaultValue }
        )
    }
}