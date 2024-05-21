package com.anafthdev.story.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.story.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
): ViewModel() {

    val stories = storyRepository.getStories()

    fun getStoriesWithLocation(
        onError: (Exception) -> Unit,
        onSuccess: (List<String>) -> Unit
    ) {
        viewModelScope.launch {
            val storyList = storyRepository.getAllStoriesFromDb().firstOrNull()

            if (storyList == null) onError(Exception("No story with any location"))
            else onSuccess(storyList.filter { it.lat != 0.0 && it.lon != 0.0 }.map { it.id })
        }
    }

}