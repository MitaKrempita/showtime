package com.example.showtime.infrastructure.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "movie_genre_junction",
    primaryKeys = ["imdbId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["imdbId"],
            childColumns = ["imdbId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GenreEntity::class,
            parentColumns = ["id"],
            childColumns = ["genreId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MovieGenreEntity(
    val imdbId: String,
    val genreId: Int
)