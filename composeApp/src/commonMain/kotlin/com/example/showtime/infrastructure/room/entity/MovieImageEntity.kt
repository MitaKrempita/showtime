package com.example.showtime.infrastructure.room.entity

import androidx.room.Entity

@Entity(
    tableName = "movie_images",
    primaryKeys = ["movieId", "filePath", "imageType"]
)
data class MovieImageEntity(
    val movieId: String,
    val filePath: String,
    val imageType: String,
    val width: Int?,
    val height: Int?,
    val voteAverage: Float?,
    val language: String?
)
