package com.anafthdev.story.data.datasource.remote

import com.anafthdev.story.data.model.body.LoginRequestBody
import com.anafthdev.story.data.model.body.RegisterRequestBody
import com.anafthdev.story.data.model.response.LoginResponse
import com.anafthdev.story.data.model.response.PostStoryResponse
import com.anafthdev.story.data.model.response.RegisterResponse
import com.anafthdev.story.data.model.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.QueryMap

interface StoryApiService {

    /**
     * Create new user
     */
    @POST("/v1/register")
    suspend fun register(
        @Body body: RegisterRequestBody
    ): Response<RegisterResponse>

    /**
     * Login
     */
    @POST("/v1/login")
    suspend fun login(
        @Body body: LoginRequestBody
    ): Response<LoginResponse>

    /**
     * Post story
     */
    @Multipart
    @POST("/v1/stories")
    suspend fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
    ): Response<PostStoryResponse>

    /**
     * Get all stories
     *
     * @param optionalQuery query opsional yaitu `{page: int, size: int, location: int (1 or 0)}`
     */
    @GET("/v1/stories")
    suspend fun stories(
        @QueryMap optionalQuery: Map<String, Int>
    ): Response<StoriesResponse>

}