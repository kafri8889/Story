package com.anafthdev.story.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anafthdev.story.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    storyRepository: StoryRepository,
): ViewModel() {

    // Tambahkan "cachedIn" supaya tidak crash saat berpindah screen
    val stories = storyRepository.getStories()

    /**
     * For testing purpose
     */
    val storiesWithoutCached = storyRepository.getStories()

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

}