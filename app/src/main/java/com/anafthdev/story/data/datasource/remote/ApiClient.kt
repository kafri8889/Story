package com.anafthdev.story.data.datasource.remote

import com.anafthdev.story.BuildConfig
import com.anafthdev.story.foundation.common.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiClient {

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
        }
    }

    private fun getAuthInterceptor(sessionManager: SessionManager): Interceptor {
        return Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .apply {
                        if (sessionManager.token != null) {
                            addHeader("Authorization", "Bearer ${sessionManager.token}")
                        }
                    }
                    .build()
            )
        }
    }

    fun getClient(sessionManager: SessionManager): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(getAuthInterceptor(sessionManager))
            .addInterceptor(loggingInterceptor)
            .build()
    }

}