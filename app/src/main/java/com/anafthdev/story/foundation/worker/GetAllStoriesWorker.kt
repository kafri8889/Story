package com.anafthdev.story.foundation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.anafthdev.story.data.model.response.ErrorResponse
import com.anafthdev.story.data.repository.StoryRepository
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker for get all stories request.
 */
@HiltWorker
class GetAllStoriesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val storyRepository: StoryRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return WorkerUtil.tryWork(WorkerUtil.EXTRA_ERROR_MESSAGE) {
            val page = inputData.getInt(EXTRA_PAGE, -1)
            val size = inputData.getInt(EXTRA_SIZE, -1)
            val location = inputData.getInt(EXTRA_LOCATION, 0)
            val optionalQuery = mutableMapOf<String, Int>().apply {
                put("location", location)
            }

            when {
                page > -1 -> optionalQuery["page"] = page
                size > -1 -> optionalQuery["size"] = size
            }

            val response = storyRepository.stories(optionalQuery)
            val body = response.body()

            if (response.isSuccessful && body?.error == false) {
                response.body()?.let {
                    storyRepository.insertStoryToDb(*it.listStory.toTypedArray())
                }

                Result.success()
            } else {
                val errorResponse = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )

                Result.failure(
                    workDataOf(
                        WorkerUtil.EXTRA_ERROR_MESSAGE to (errorResponse?.message ?: "Unknown error")
                    )
                )
            }
        }
    }

    companion object {
        const val EXTRA_PAGE = "page"
        const val EXTRA_SIZE = "size"
        const val EXTRA_LOCATION = "location"
    }

}