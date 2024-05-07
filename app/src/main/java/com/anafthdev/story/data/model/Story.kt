package com.anafthdev.story.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories_table")
data class Story(
    @PrimaryKey
    @ColumnInfo(name = "id_stories") val id: String,
    @ColumnInfo(name = "createdAt_stories") val createdAt: String,
    @ColumnInfo(name = "description_stories") val description: String,
    @ColumnInfo(name = "lat_stories") val lat: Double,
    @ColumnInfo(name = "lon_stories") val lon: Double,
    @ColumnInfo(name = "name_stories") val name: String,
    @ColumnInfo(name = "photoUrl_stories") val photoUrl: String
)