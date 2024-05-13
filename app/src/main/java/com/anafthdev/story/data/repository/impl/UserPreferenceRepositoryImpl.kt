package com.anafthdev.story.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import com.anafthdev.story.ProtoUserPreference
import com.anafthdev.story.data.Language
import com.anafthdev.story.data.model.UserPreference
import com.anafthdev.story.data.repository.UserPreferenceRepository
import com.anafthdev.story.data.serializer.UserPreferenceSerializer
import com.anafthdev.story.foundation.extension.toUserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor(
    private val userPreferenceDataStore: DataStore<ProtoUserPreference>
): UserPreferenceRepository {

    override val userPreferences: Flow<UserPreference>
        get() = userPreferenceDataStore.data.map { it.toUserPreference() }

    override suspend fun setLanguage(language: Language) {
        userPreferenceDataStore.updateData {
            it.copy(
                language = language.code
            )
        }
    }

    companion object {
        const val DATASTORE_NAME = "user_preference"

        val corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { UserPreferenceSerializer.defaultValue }
        )
    }

}