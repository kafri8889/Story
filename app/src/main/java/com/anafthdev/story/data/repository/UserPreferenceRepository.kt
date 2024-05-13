package com.anafthdev.story.data.repository

import com.anafthdev.story.data.Language
import com.anafthdev.story.data.model.UserPreference
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {

    val userPreferences: Flow<UserPreference>

    suspend fun setLanguage(language: Language)
}