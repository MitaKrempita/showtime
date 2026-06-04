package com.example.showtime.infrastructure.api.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.domain.api.QuizApi
import com.example.showtime.infrastructure.api.login.dto.LeaderboardEntryDTO
import com.example.showtime.infrastructure.api.login.dto.movie.PaginatedResponseDTO
import com.example.showtime.infrastructure.data.quiz.PostQuizResultResponse
import com.example.showtime.infrastructure.data.quiz.QuizRequest
import com.example.showtime.infrastructure.data.quiz.QuizResult
import com.example.showtime.infrastructure.datastore.getValidToken
import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.network.AppExceptionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val BASE_URL = "https://rma.finlab.rs"

class KtorQuizApi(
    private val client : HttpClient,
    private val dataStore: DataStore<Preferences>
) : QuizApi {

    override suspend fun leaderboardGet(page: Int?, pageSize: Int?): ApiResult<PaginatedResponseDTO<LeaderboardEntryDTO>> {
        return try {
            val response = client.get("$BASE_URL/leaderboard") {
                url {
                    parameters.append("category", "1")
                    page?.let { parameters.append("page", it.toString()) }
                    pageSize?.let { parameters.append("page_size", it.toString()) }
                }
            }
            if (!response.status.isSuccess()) {
                return createExceptionResult(response)
            }
            ApiResult.Success(response.body())
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("NetworkError", 0, e.message ?: "Unknown connection error."))
        }
    }

    override suspend fun leaderboardPost(quizRequest: QuizRequest): ApiResult<PostQuizResultResponse> {
        return try {
            val response = client.post("$BASE_URL/leaderboard") {
                getValidToken(dataStore)?.let { header("Authorization", "Bearer $it") }
                contentType(ContentType.Application.Json)
                setBody(quizRequest)
            }
            if (!response.status.isSuccess()) {
                return createExceptionResult(response)
            }
            ApiResult.Success(response.body())
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("NetworkError", 0, e.message ?: "Unknown connection error."))
        }
    }

    override suspend fun quizResults(page: Int?, pageSize: Int?): ApiResult<PaginatedResponseDTO<QuizResult>> {
        return try {
            val response = client.get("$BASE_URL/me/quiz-results") {
                getValidToken(dataStore)?.let { header("Authorization", "Bearer $it") }
                url {
                    page?.let { parameters.append("page", it.toString()) }
                    pageSize?.let { parameters.append("page_size", it.toString()) }
                }
            }
            if (!response.status.isSuccess()) {
                return createExceptionResult(response)
            }
            ApiResult.Success(response.body())
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("NetworkError", 0, e.message ?: "Unknown connection error."))
        }
    }

    private suspend fun createExceptionResult(response : HttpResponse) : ApiResult<Nothing> {
        return try {
            val jsonElements = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val errorJson : String= jsonElements["error"]?.jsonPrimitive?.content?:""
            val httpCodeJson : Int = jsonElements["httpCode"]?.jsonPrimitive?.int ?: response.status.value
            val messageJson : String= jsonElements["message"]?.jsonPrimitive?.content?:""
            ApiResult.Error(AppExceptionResponse(errorJson,httpCodeJson,messageJson))
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("ParsingError", response.status.value, "Failed to parse error response."))
        }
    }
}
