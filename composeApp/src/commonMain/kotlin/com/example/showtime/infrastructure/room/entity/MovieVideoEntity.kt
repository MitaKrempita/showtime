package com.example.showtime.infrastructure.room.entity

import androidx.room.Entity

@Entity(
    tableName = "movie_videos",
    primaryKeys = ["movieId", "key"]
)
data class MovieVideoEntity(
    val movieId: String,
    val key: String,
    val site: String,
    val name: String?,
    val type: String?,
    val official: Boolean,
    val publishedAt: String?
)
