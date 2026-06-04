package com.example.showtime.infrastructure.data.quiz

import kotlinx.serialization.Serializable


@Serializable
data class PostQuizResultResponse(
    val results : List<QuizResult>,
    val ranking : Int
)
