package com.example.showtime.domain.model

import com.example.showtime.presentation.quiz.main.QuestionType

data class QuizQuestion(
    val type: QuestionType,
    val imageUrl: String,
    val movieTitle: String,
    val options: List<String>,
    val correctAnswer: String
)