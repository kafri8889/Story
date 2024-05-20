package com.anafthdev.story.data.repository.impl

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.anafthdev.story.data.datasource.StoryRemoteMediator
import com.anafthdev.story.data.datasource.local.AppDatabase
import com.anafthdev.story.data.datasource.local.StoryDao
import com.anafthdev.story.data.datasource.remote.StoryApiService
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.data.model.body.LoginRequestBody
import com.anafthdev.story.data.model.body.RegisterRequestBody
import com.anafthdev.story.data.model.response.LoginResponse
import com.anafthdev.story.data.model.response.PostStoryResponse
import com.anafthdev.story.data.model.response.RegisterResponse
import com.anafthdev.story.data.model.response.StoriesResponse
import com.anafthdev.story.data.repository.StoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyApiService: StoryApiService,
    private val appDatabase: AppDatabase,
    private val storyDao: StoryDao
): StoryRepository {

    override suspend fun register(body: RegisterRequestBody): Response<RegisterResponse> {
        return storyApiService.register(body)
    }

    override suspend fun login(body: LoginRequestBody): Response<LoginResponse> {
        return storyApiService.login(body)
    }

    override suspend fun postStory(file: MultipartBody.Part, description: RequestBody): Response<PostStoryResponse> {
        return storyApiService.postStory(file, description)
    }

    override suspend fun stories(optionalQuery: Map<String, Int>): Response<StoriesResponse> {
        return storyApiService.stories(optionalQuery)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(

    ): LiveData<PagingData<Story>> {
        return Pager(
            remoteMediator = StoryRemoteMediator(appDatabase, storyApiService),
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                appDatabase.storyDao().getAllStories()
            }
        ).liveData.cachedIn(CoroutineScope(SupervisorJob() + Dispatchers.Main))
    }

    override fun getStoryFromDb(id: String): Flow<Story?> {
        return storyDao.getStoryById(id)
    }

    override fun getAllStoriesFromDb(): Flow<List<Story>> {
        return storyDao.getAllStoriesAsFlow()
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