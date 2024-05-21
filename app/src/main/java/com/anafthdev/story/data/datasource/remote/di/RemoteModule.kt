package com.anafthdev.story.data.datasource.remote.di

import com.anafthdev.story.BuildConfig
import com.anafthdev.story.data.datasource.remote.ApiClient
import com.anafthdev.story.data.datasource.remote.StoryApiService
import com.anafthdev.story.foundation.common.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        sessionManager: SessionManager
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiClient.API_BASE_URL_TEST ?: BuildConfig.STORY_BASE_URL)
            .client(ApiClient.getClient(sessionManager))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideStoryApiService(
        retrofit: Retrofit
    ): StoryApiService = retrofit.create(StoryApiService::class.java)

}