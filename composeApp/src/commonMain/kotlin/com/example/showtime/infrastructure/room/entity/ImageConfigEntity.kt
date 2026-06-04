package com.example.showtime.infrastructure.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_config")
data class ImageConfigEntity(
    @PrimaryKey val key: String,
    val value: String
)
