package com.anafthdev.story.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.data.model.body.LoginRequestBody
import com.anafthdev.story.data.model.body.RegisterRequestBody
import com.anafthdev.story.data.model.response.LoginResponse
import com.anafthdev.story.data.model.response.PostStoryResponse
import com.anafthdev.story.data.model.response.RegisterResponse
import com.anafthdev.story.data.model.response.StoriesResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface StoryRepository {

    /**
     * Create new user
     */
    suspend fun register(body: RegisterRequestBody): Response<RegisterResponse>

    /**
     * Login
     */
    suspend fun login(body: LoginRequestBody): Response<LoginResponse>

    /**
     * Post story to server
     */
    suspend fun postStory(file: MultipartBody.Part, description: RequestBody): Response<PostStoryResponse>

    /**
     * Get all stories
     *
     * @param optionalQuery query opsional yaitu `{page: int, size: int, location: int (1 or 0)}`
     */
    suspend fun stories(optionalQuery: Map<String, Int>): Response<StoriesResponse>

    fun getStories(): LiveData<PagingData<Story>>

    fun getStoryFromDb(id: String): Flow<Story?>

    fun getAllStoriesFromDb(): Flow<List<Story>>

    suspend fun updateStoryInDb(vararg story: Story)

    suspend fun deleteStoryFromDb(vararg story: Story)

    suspend fun insertStoryToDb(vararg story: Story)

}