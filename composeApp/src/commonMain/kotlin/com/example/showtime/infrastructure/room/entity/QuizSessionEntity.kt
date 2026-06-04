package com.example.showtime.infrastructure.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_sessions")
data class QuizSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Float,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val timeUsed: Int,
    val timestamp: Long
)