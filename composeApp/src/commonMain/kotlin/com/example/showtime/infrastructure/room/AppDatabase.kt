package com.example.showtime.infrastructure.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.showtime.infrastructure.room.dao.MovieDao
import com.example.showtime.infrastructure.room.dao.QuizDao
import com.example.showtime.infrastructure.room.entity.*

@Database(
    entities = [
        MovieEntity::class,
        CollectionEntity::class,
        GenreEntity::class,
        MovieGenreEntity::class,
        FavoriteEntity::class,
        WatchlistEntity::class,
        PersonSummaryEntity::class,
        MovieCastEntity::class,
        QuizSessionEntity::class,
        MovieImageEntity::class,
        MovieVideoEntity::class,
        ImageConfigEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun quizDao(): QuizDao
}
