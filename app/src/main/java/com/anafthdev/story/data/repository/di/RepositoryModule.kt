package com.anafthdev.story.data.repository.di

import androidx.datastore.core.DataStore
import com.anafthdev.story.ProtoUserCredential
import com.anafthdev.story.ProtoUserPreference
import com.anafthdev.story.data.datasource.local.StoryDao
import com.anafthdev.story.data.datasource.remote.StoryApiService
import com.anafthdev.story.data.repository.StoryRepository
import com.anafthdev.story.data.repository.UserCredentialRepository
import com.anafthdev.story.data.repository.UserPreferenceRepository
import com.anafthdev.story.data.repository.impl.StoryRepositoryImpl
import com.anafthdev.story.data.repository.impl.UserCredentialRepositoryImpl
import com.anafthdev.story.data.repository.impl.UserPreferenceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideStoryApiRepository(
        storyApiService: StoryApiService,
        storyDao: StoryDao
    ): StoryRepository = StoryRepositoryImpl(storyApiService, storyDao)

    @Provides
    @Singleton
    fun provideUserCredentialRepository(
        userCredentialDataStore: DataStore<ProtoUserCredential>
    ): UserCredentialRepository = UserCredentialRepositoryImpl(userCredentialDataStore)

    @Provides
    @Singleton
    fun provideUserPreferenceRepository(
        userPreferenceDataStore: DataStore<ProtoUserPreference>
    ): UserPreferenceRepository = UserPreferenceRepositoryImpl(userPreferenceDataStore)

}