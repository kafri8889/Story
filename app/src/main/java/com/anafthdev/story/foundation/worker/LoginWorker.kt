package com.anafthdev.story.foundation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.anafthdev.story.data.model.LoginResult
import com.anafthdev.story.data.model.body.LoginRequestBody
import com.anafthdev.story.data.model.response.ErrorResponse
import com.anafthdev.story.data.repository.StoryRepository
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker for login request.
 *
 * If request is successful, return [LoginResult] as work output data
 */
@HiltWorker
class LoginWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val storyRepository: StoryRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return WorkerUtil.tryWork(WorkerUtil.EXTRA_ERROR_MESSAGE) {
            val requestBodyJson = inputData.getString(EXTRA_REQUEST_BODY) ?: throw IllegalArgumentException(
                "Request body is null, please insert request body!"
            )

            val response = storyRepository.login(
                Gson().fromJson(requestBodyJson, LoginRequestBody::class.java)
            )

            val body = response.body()

            if (response.isSuccessful && body?.error == false) Result.success(
                workDataOf(
                    WorkerUtil.EXTRA_OUTPUT to Gson().toJson(body.loginResult)
                )
            ) else {
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
        const val EXTRA_REQUEST_BODY = "request_body"
    }

}