package com.anafthdev.story.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.story.data.model.response.ErrorResponse
import com.anafthdev.story.data.repository.StoryRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
): ViewModel() {

    val stories = storyRepository.getStories()

    /**
     * Mengambil 30 cerita terbaru
     */
    fun getStoriesWithLocation(
        onError: (Exception) -> Unit,
        onSuccess: (List<String>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = storyRepository.stories(
                    mapOf(
                        "size" to 30,
                        "location" to 1
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.listStory.filter { it.lat != 0.0 && it.lon != 0.0 }.map { it.id })
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )

                    onError(Exception(errorResponse.message))
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}