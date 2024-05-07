package com.anafthdev.story.data.repository.impl

import com.anafthdev.story.data.datasource.local.StoryDao
import com.anafthdev.story.data.datasource.remote.StoryApiService
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.data.model.body.LoginRequestBody
import com.anafthdev.story.data.model.body.RegisterRequestBody
import com.anafthdev.story.data.model.response.LoginResponse
import com.anafthdev.story.data.model.response.RegisterResponse
import com.anafthdev.story.data.model.response.StoriesResponse
import com.anafthdev.story.data.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyApiService: StoryApiService,
    private val storyDao: StoryDao
): StoryRepository {

    override suspend fun register(body: RegisterRequestBody): Response<RegisterResponse> {
        return storyApiService.register(body)
    }

    override suspend fun login(body: LoginRequestBody): Response<LoginResponse> {
        return storyApiService.login(body)
    }

    override suspend fun stories(optionalQuery: Map<String, Int>): Response<StoriesResponse> {
        return storyApiService.stories(optionalQuery)
    }

    override fun getAllStoriesFromDb(): Flow<List<Story>> {
        return storyDao.getAllStories()
    }

    override fun getStoryFromDb(id: String): Flow<Story?> {
        return storyDao.getStoryById(id)
    }

    override suspend fun updateStoryInDb(vararg story: Story) {
        storyDao.updateStory(*story)
    }

    override suspend fun deleteStoryFromDb(vararg story: Story) {
        storyDao.deleteStory(*story)
    }

    override suspend fun insertStoryToDb(vararg story: Story) {
        storyDao.insertStory(*story)
    }
}