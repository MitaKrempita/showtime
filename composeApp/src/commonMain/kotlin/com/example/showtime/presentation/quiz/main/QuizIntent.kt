package com.example.showtime.presentation.quiz.main

sealed interface QuizIntent {
    data class SelectAnswer(val answer: String) : QuizIntent
    data object AbandonQuiz : QuizIntent
}
