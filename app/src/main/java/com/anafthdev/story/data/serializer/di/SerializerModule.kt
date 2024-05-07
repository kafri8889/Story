package com.anafthdev.story.data.serializer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.anafthdev.story.ProtoUserCredential
import com.anafthdev.story.data.repository.impl.UserCredentialRepositoryImpl
import com.anafthdev.story.data.serializer.UserCredentialSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SerializerModule {

    @Provides
    @Singleton
    fun provideUserCredentialDataStore(
        @ApplicationContext context: Context
    ): DataStore<ProtoUserCredential> = DataStoreFactory.create(
        serializer = UserCredentialSerializer,
        corruptionHandler = UserCredentialRepositoryImpl.corruptionHandler,
        produceFile = { context.dataStoreFile(UserCredentialRepositoryImpl.DATASTORE_NAME) }
    )

}