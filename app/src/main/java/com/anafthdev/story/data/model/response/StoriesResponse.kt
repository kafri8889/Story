package com.anafthdev.story.data.model.response

import com.anafthdev.story.data.model.Story

data class StoriesResponse(
    val error: Boolean,
    val listStory: List<Story>,
    val message: String
)