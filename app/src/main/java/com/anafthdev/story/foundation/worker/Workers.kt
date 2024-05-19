package com.anafthdev.story.foundation.worker

import android.net.Uri
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.anafthdev.story.data.model.LoginResult
import com.anafthdev.story.data.model.body.LoginRequestBody
import com.anafthdev.story.data.model.body.RegisterRequestBody
import com.google.gson.Gson

object Workers {

    /**
     * Worker for register (create new account) request.
     */
    fun register(body: RegisterRequestBody): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<RegisterWorker>()
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED
                )
            )
            .setInputData(
                workDataOf(
                    RegisterWorker.EXTRA_REQUEST_BODY to Gson().toJson(body)
                )
            )
            .build()
    }

    /**
     * Worker for login request.
     *
     * If request is successful, return [LoginResult] as work output data
     */
    fun login(body: LoginRequestBody): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<LoginWorker>()
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED
                )
            )
            .setInputData(
                workDataOf(
                    LoginWorker.EXTRA_REQUEST_BODY to Gson().toJson(body)
                )
            )
            .build()
    }

    fun postStory(imageUri: Uri, description: String): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<PostStoryWorker>()
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED
                )
            )
            .setInputData(
                workDataOf(
                    PostStoryWorker.EXTRA_IMAGE_URI to imageUri.toString(),
                    PostStoryWorker.EXTRA_IMAGE_DESCRIPTION to description
                )
            )
            .build()
    }

}