package com.example.showtime.infrastructure.data.quiz

import kotlinx.serialization.Serializable

@Serializable
data class QuizRequest(
    val score : Float,
    val category : Int
)
