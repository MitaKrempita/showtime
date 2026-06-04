package com.example.showtime.presentation.quiz.start

import com.example.showtime.domain.model.LeaderboardEntry

data class QuizStartState(
    val leaderBoardData: List<LeaderboardEntry> = emptyList(),
    val personalBest: Float = 0f,
    val gamesPlayed: Int = 0,
    val isLoading: Boolean = false,
    val canStartQuiz: Boolean = false,
    val errorMessage: String? = null
)