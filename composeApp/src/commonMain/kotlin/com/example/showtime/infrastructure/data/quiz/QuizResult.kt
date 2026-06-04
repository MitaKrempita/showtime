package com.example.showtime.infrastructure.data.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizResult(
    val id : Int,
    val category : Int,
    val score : Float,
    val playedAt : Long
)
