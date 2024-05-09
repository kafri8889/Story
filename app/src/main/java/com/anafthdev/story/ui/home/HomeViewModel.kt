package com.anafthdev.story.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Operation
import androidx.work.WorkManager
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.data.repository.StoryRepository
import com.anafthdev.story.data.repository.UserCredentialRepository
import com.anafthdev.story.foundation.worker.Workers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userCredentialRepository: UserCredentialRepository,
    private val storyRepository: StoryRepository,
    private val workManager: WorkManager
): ViewModel() {

    private val _stories = MutableLiveData(emptyList<Story>())
    val stories: LiveData<List<Story>> = _stories

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    init {
        viewModelScope.launch {
            storyRepository.getAllStoriesFromDb().collectLatest { storyList ->
                _stories.value = storyList.sortedByDescending { it.createdAt } // Sort by latest
            }
        }
    }

    fun refreshStories() {
        _isRefreshing.value = true

        workManager.beginWith(
            Workers.getAllStories()
        ).enqueue().state.let { liveData: LiveData<Operation.State> ->
            liveData.observeForever(object : Observer<Operation.State> {
                override fun onChanged(value: Operation.State) {
                    when (value) {
                        is Operation.State.SUCCESS, is Operation.State.FAILURE -> {
                            _isRefreshing.value = false
                            liveData.removeObserver(this)
                        }
                    }
                }
            })
        }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            userCredentialRepository.clear()
            onLogout()
        }
    }

}