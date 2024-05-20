package com.anafthdev.story.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anafthdev.story.data.model.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(): ViewModel() {

    private val _story = MutableLiveData<Story?>(null)
    val story: LiveData<Story?> = _story

    fun setStory(story: Story) {
        _story.value = story
    }

}