package com.example.showtime.domain.repository

import com.example.showtime.domain.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    suspend fun getLocalMoviesCount(): Int
    suspend fun getLocalBestScore(): Float
    suspend fun getLocalGamesPlayedCount(): Int
    suspend fun getLeaderboard(page: Int, pageSize: Int): Result<List<LeaderboardEntry>>
    suspend fun saveQuizResult(score: Float, correct: Int, incorrect: Int, timeUsed: Int): Result<Unit>
    suspend fun syncQuizResults(page: Int, pageSize: Int): Result<Unit>
}