package com.example.showtime.presentation.quiz.start

sealed interface QuizStartIntent {
    data object LoadData : QuizStartIntent
    data object Retry : QuizStartIntent
}