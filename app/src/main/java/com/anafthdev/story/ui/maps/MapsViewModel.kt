package com.anafthdev.story.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MapsViewModel @Inject constructor(
    private val storyRepository: StoryRepository
): ViewModel() {

    private val _storyIds = MutableStateFlow<Array<String>>(emptyArray())

    private val _stories = MutableLiveData<List<Story>>(emptyList())
    val stories: LiveData<List<Story>> = _stories

    init {
        viewModelScope.launch {
            _storyIds.flatMapLatest { ids ->
                storyRepository.getStoryFromDb(*ids)
            }.filterNot { it.isEmpty() }.collectLatest { storyList ->
                _stories.value = storyList
            }
        }
    }

    fun getStories(storyIds: Array<String>) {
        viewModelScope.launch {
            _storyIds.emit(storyIds)
        }
    }

}