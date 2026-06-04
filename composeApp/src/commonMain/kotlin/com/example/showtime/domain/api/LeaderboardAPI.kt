package com.example.showtime.domain.api

import com.example.showtime.infrastructure.api.login.dto.LeaderboardEntryDTO
import com.example.showtime.infrastructure.api.login.dto.movie.PaginatedResponseDTO
import com.example.showtime.infrastructure.data.quiz.PostQuizResultResponse
import com.example.showtime.infrastructure.data.quiz.QuizRequest
import com.example.showtime.infrastructure.data.quiz.QuizResult
import com.example.showtime.infrastructure.network.ApiResult

interface QuizApi
{
    suspend fun leaderboardGet(page: Int?, pageSize: Int?): ApiResult<PaginatedResponseDTO<LeaderboardEntryDTO>>
    suspend fun leaderboardPost(quizRequest: QuizRequest): ApiResult<PostQuizResultResponse>
    suspend fun quizResults(page: Int?, pageSize: Int?): ApiResult<PaginatedResponseDTO<QuizResult>>
}