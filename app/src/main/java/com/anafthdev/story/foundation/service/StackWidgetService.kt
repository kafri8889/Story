package com.anafthdev.story.foundation.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.anafthdev.story.data.repository.StoryRepository
import com.anafthdev.story.foundation.factory.StackRemoteViewsFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StackWidgetService: RemoteViewsService() {

    @Inject lateinit var storyRepository: StoryRepository

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        StackRemoteViewsFactory(this.applicationContext, storyRepository)
}