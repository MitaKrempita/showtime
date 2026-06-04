package com.example.showtime.infrastructure.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class PersonSummaryEntity(
    @PrimaryKey val imdbId: String,
    val name: String,
    val professions: String?,
    val department: String?,
    val profilePath: String?
)