package com.example.showtime.infrastructure.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "movie_cast_junction",
    primaryKeys = ["movieId", "personId"],
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["imdbId"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PersonSummaryEntity::class,
            parentColumns = ["imdbId"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MovieCastEntity(
    val movieId: String,
    val personId: String,
    val sortOrder: Int = 0
)
