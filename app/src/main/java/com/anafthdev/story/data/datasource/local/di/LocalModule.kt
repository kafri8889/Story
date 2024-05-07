package com.anafthdev.story.data.datasource.local.di

import android.content.Context
import androidx.room.Room
import com.anafthdev.story.data.datasource.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
        .build()

    @Provides
    @Singleton
    fun provideStoryDao(appDatabase: AppDatabase) = appDatabase.storyDao()
}