package com.example.showtime.infrastructure.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "movies",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class MovieEntity(
    @PrimaryKey val imdbId: String,
    val collectionId: Int?,
    val tmdbId: Int?,
    val title: String,
    val originalTitle: String?,
    val overview: String?,
    val tagline: String?,
    val releaseDate: String?,
    val year: Int?,
    val runtime: Int?,
    val budget: Long?,
    val revenue: Long?,
    val languageCode: String?,
    val popularity: Float?,
    val imdbRating: Float?,
    val imdbVotes: Int?,
    val tmdbRating: Float?,
    val tmdbVotes: Int?,
    val posterPath: String?,
    val backdropPath: String?,
    val homepage: String?
)