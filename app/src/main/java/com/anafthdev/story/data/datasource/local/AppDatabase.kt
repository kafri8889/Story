package com.anafthdev.story.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anafthdev.story.data.model.Story

@Database(
    entities = [
        Story::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun storyDao(): StoryDao

}