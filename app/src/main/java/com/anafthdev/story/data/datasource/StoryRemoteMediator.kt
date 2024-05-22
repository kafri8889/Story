package com.anafthdev.story.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.anafthdev.story.data.datasource.local.AppDatabase
import com.anafthdev.story.data.datasource.remote.StoryApiService
import com.anafthdev.story.data.model.RemoteKeys
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.data.model.response.ErrorResponse
import com.anafthdev.story.foundation.util.wrapEspressoIdlingResource
import com.google.gson.Gson

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val appDatabase: AppDatabase,
    private val storyApiService: StoryApiService
): RemoteMediator<Int, Story>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> INITIAL_PAGE_INDEX
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        wrapEspressoIdlingResource {
            try {
                val response = storyApiService.stories(
                    mapOf(
                        "page" to page,
                        "size" to state.config.pageSize,
                        "location" to 0
                    )
                )

                if (!response.isSuccessful) {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )

                    return MediatorResult.Error(Exception(errorResponse.message))
                }

                val stories = response.body()!!.listStory.toTypedArray()
                val endOfPaginationReached = false

                appDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        appDatabase.storyDao().deleteAll()
                        appDatabase.remoteKeysDao().deleteAll()
                    }

                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = stories.map {
                        RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }

                    appDatabase.remoteKeysDao().insertAll(keys)
                    appDatabase.storyDao().insertStory(*stories)
                }

                return MediatorResult.Success(endOfPaginationReached)
            } catch (exception: Exception) {
                return MediatorResult.Error(exception)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            appDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            appDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}