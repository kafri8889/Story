package com.anafthdev.story.foundation.common.di

import com.anafthdev.story.data.repository.UserCredentialRepository
import com.anafthdev.story.foundation.common.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    @Singleton
    fun provideSessionManager(
        userCredentialRepository: UserCredentialRepository
    ): SessionManager = SessionManager(userCredentialRepository)

}