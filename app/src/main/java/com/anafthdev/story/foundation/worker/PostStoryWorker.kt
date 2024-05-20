package com.anafthdev.story.foundation.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.anafthdev.story.data.model.response.ErrorResponse
import com.anafthdev.story.data.repository.StoryRepository
import com.anafthdev.story.foundation.util.UriUtil
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

/**
 * Worker for post story request.
 */
@HiltWorker
class PostStoryWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val storyRepository: StoryRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return WorkerUtil.tryWork(WorkerUtil.EXTRA_ERROR_MESSAGE) {
            val uri = inputData.getString(EXTRA_IMAGE_URI)?.toUri() ?: throw IllegalArgumentException("Image URI is null")
            val description = inputData.getString(EXTRA_IMAGE_DESCRIPTION) ?: throw IllegalArgumentException("Image description is null")
            val file = UriUtil.uriToFile(context, uri)
            val location = try {
                Gson().fromJson(inputData.getString(EXTRA_LOCATION), LatLng::class.java)
            } catch (e: Exception) {
                Timber.e(e)
                null
            }

            Timber.i("Uploading file: ${file.path}")

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val latitude = (location?.latitude ?: 0.0).toString().toRequestBody("text/plain".toMediaType())
            val longitude = (location?.longitude ?: 0.0).toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val response = storyRepository.postStory(multipartBody, requestBody, latitude, longitude)
            val body = response.body()

            if (response.isSuccessful && body?.error == false) Result.success() else {
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

        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_LOCATION = "extra_location"
        const val EXTRA_IMAGE_DESCRIPTION = "extra_image_description"

    }
}
