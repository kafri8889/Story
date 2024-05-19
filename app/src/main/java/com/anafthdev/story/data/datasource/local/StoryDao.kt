package com.anafthdev.story.data.datasource.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.anafthdev.story.data.model.Story
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {

    @Query("SELECT * FROM stories_table")
    fun getAllStories(): PagingSource<Int, Story>

    @Query("SELECT * FROM stories_table")
    fun getAllStoriesAsFlow(): Flow<List<Story>>

    @Query("SELECT * FROM stories_table WHERE id_stories = :storyId")
    fun getStoryById(storyId: String): Flow<Story?>

    @Query("DELETE FROM stories_table")
    suspend fun deleteAll()

    @Update
    suspend fun updateStory(vararg story: Story)

    @Delete
    suspend fun deleteStory(vararg story: Story)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertStory(vararg story: Story)

}