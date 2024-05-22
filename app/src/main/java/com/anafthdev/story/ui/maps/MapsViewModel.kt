package com.anafthdev.story.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anafthdev.story.data.model.Story
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(): ViewModel() {

    private val _stories = MutableLiveData<List<Story>>(emptyList())
    val stories: LiveData<List<Story>> = _stories

    fun setStories(storyList: Array<String>) {
        _stories.value = storyList.map {
            Gson().fromJson(it, Story::class.java)
        }
    }

}