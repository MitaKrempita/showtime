package com.example.showtime.infrastructure.implementation.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.domain.api.QuizApi
import com.example.showtime.domain.model.LeaderboardEntry
import com.example.showtime.domain.repository.QuizRepository
import com.example.showtime.infrastructure.api.login.dto.movie.PaginatedResponseDTO
import com.example.showtime.infrastructure.api.login.mapper.toDomain
import com.example.showtime.infrastructure.data.quiz.QuizRequest
import com.example.showtime.infrastructure.datastore.clearAuth
import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.room.dao.MovieDao
import com.example.showtime.infrastructure.room.dao.QuizDao
import com.example.showtime.infrastructure.room.entity.QuizSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.Instant

class QuizRepositoryImpl(
    private val movieDao: MovieDao,
    private val quizDao: QuizDao,
    private val quizApi: QuizApi,
    private val dataStore: DataStore<Preferences>
) : QuizRepository {

    private suspend fun logoutLocally() {
        clearAuth(dataStore)
        movieDao.clearFavorites()
        movieDao.clearWatchlist()
    }

    private suspend fun <T> ApiResult<T>.getOrThrow(): T {
        return when (this) {
            is ApiResult.Success -> data
            is ApiResult.Error -> {
                if (message.httpCode == 401) {
                    logoutLocally()
                }
                throw Exception(message.message)
            }
        }
    }

    override suspend fun getLocalMoviesCount(): Int = withContext(Dispatchers.IO) {
        try {
            movieDao.countQuizReadyMovies()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getLocalBestScore(): Float = withContext(Dispatchers.IO) {
        try {
            quizDao.getBestScore().first() ?: 0f
        } catch (e: Exception) {
            0f
        }
    }

    override suspend fun getLocalGamesPlayedCount(): Int = withContext(Dispatchers.IO) {
        try {
            quizDao.getGamesPlayedCount().first()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getLeaderboard(page: Int, pageSize: Int): Result<List<LeaderboardEntry>> = withContext(Dispatchers.IO) {
        when (val result = quizApi.leaderboardGet(page, pageSize)) {
            is ApiResult.Success -> {
                Result.success(result.data.items.map { it.toDomain() })
            }
            is ApiResult.Error -> {
                Result.failure(Exception(result.message.message))
            }
        }
    }

    override suspend fun saveQuizResult(
        score: Float,
        correct: Int,
        incorrect: Int,
        timeUsed: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val session = QuizSessionEntity(
                score = score,
                correctAnswers = correct,
                incorrectAnswers = incorrect,
                timeUsed = timeUsed,
                timestamp = Instant.now().toEpochMilli()
            )
            quizDao.insertSession(session)

            val request = QuizRequest(score = score, category = 1)
            when (val apiResult = quizApi.leaderboardPost(request)) {
                is ApiResult.Success -> Result.success(Unit)
                is ApiResult.Error -> {
                    if (apiResult.message.httpCode == 401) {
                        logoutLocally()
                    }
                    Result.failure(Exception(apiResult.message.message))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncQuizResults(page: Int, pageSize: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val apiResult = quizApi.quizResults(page, pageSize).getOrThrow()
            val entities = apiResult.items.map { dto ->
                QuizSessionEntity(
                    id = dto.id,
                    score = dto.score,
                    correctAnswers = 0,
                    incorrectAnswers = 0,
                    timeUsed = 0,
                    timestamp = dto.playedAt
                )
            }
            quizDao.insertSessions(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
