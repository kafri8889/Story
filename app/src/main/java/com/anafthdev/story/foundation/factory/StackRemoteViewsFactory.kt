package com.anafthdev.story.foundation.factory

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.anafthdev.story.R
import com.anafthdev.story.data.repository.StoryRepository
import com.anafthdev.story.foundation.widget.StoryWidget
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StackRemoteViewsFactory(
    private val context: Context,
    private val storyRepository: StoryRepository
): RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private val appWidgetManager = AppWidgetManager.getInstance(context)
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        coroutineScope.launch(Dispatchers.Main) {
            storyRepository.getAllStoriesFromDb().map { storyList -> storyList.sortedByDescending { it.createdAt } }.collectLatest { storyList ->
                withContext(Dispatchers.Main.immediate) {
                    mWidgetItems.clear()
                    val ids = appWidgetManager.getAppWidgetIds(
                        ComponentName(
                            context,
                            StoryWidget::class.java
                        )
                    )

                    for (story in storyList) {
                        Glide.with(context)
                            .asBitmap()
                            .load(story.photoUrl)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {}
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    mWidgetItems.add(resource)

                                    if (mWidgetItems.size == storyList.size) {
                                        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.stack_view)
                                    }
                                }
                            })
                    }
                }
            }
        }
    }

    override fun onDataSetChanged() {

    }

    override fun onDestroy() {
        coroutineScope.cancel()
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)

        mWidgetItems.getOrNull(position)?.let { bitmap ->
            rv.setImageViewBitmap(R.id.imageView, bitmap)
        }

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}
