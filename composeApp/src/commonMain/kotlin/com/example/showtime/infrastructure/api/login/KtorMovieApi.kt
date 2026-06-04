package com.example.showtime.infrastructure.api.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.network.AppExceptionResponse
import com.example.showtime.domain.api.MovieApi
import com.example.showtime.infrastructure.api.login.dto.movie.*
import com.example.showtime.infrastructure.datastore.getValidToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val BASE_URL = "https://rma.finlab.rs"

class KtorMovieApi(
    private val client: HttpClient,
    private val dataStore: DataStore<Preferences>
) : MovieApi {
    private suspend inline fun <reified T> safeGet(
        urlString: String,
        noinline block: (HttpRequestBuilder.() -> Unit)? = null
    ): ApiResult<T> {
        return try {
            val response: HttpResponse = if (block != null) {
                client.get(urlString, block)
            } else {
                client.get(urlString)
            }

            if (!response.status.isSuccess()) {
                createExceptionResult(response)
            } else {
                ApiResult.Success(response.body<T>())
            }
        } catch (e: Exception) {
            ApiResult.Error(
                AppExceptionResponse(
                    error = "NetworkError",
                    httpCode = 0,
                    message = e.message ?: "Unknown connection error."
                )
            )
        }
    }

    private suspend fun createExceptionResult(response: HttpResponse): ApiResult<Nothing> {
        return try {
            val jsonElements = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val errorJson = jsonElements["error"]?.jsonPrimitive?.content ?: ""
            val httpCodeJson = jsonElements["httpCode"]?.jsonPrimitive?.int ?: response.status.value
            val messageJson = jsonElements["message"]?.jsonPrimitive?.content ?: ""
            ApiResult.Error(AppExceptionResponse(errorJson, httpCodeJson, messageJson))
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("ParsingError", response.status.value, "Failed to parse error response."))
        }
    }

    private suspend fun safePostUnit(urlString: String): ApiResult<Unit> {
        return try {
            val token = getValidToken(dataStore)
            val response = client.post(urlString) {
                token?.let { header("Authorization", "Bearer $it") }
            }
            if (!response.status.isSuccess()) {
                createExceptionResult(response)
            } else {
                ApiResult.Success(Unit)
            }
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("NetworkError", 0, e.message ?: "Unknown connection error."))
        }
    }

    private suspend fun safeDeleteUnit(urlString: String): ApiResult<Unit> {
        return try {
            val token = getValidToken(dataStore)
            val response = client.delete(urlString) {
                token?.let { header("Authorization", "Bearer $it") }
            }
            if (!response.status.isSuccess()) {
                createExceptionResult(response)
            } else {
                ApiResult.Success(Unit)
            }
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("NetworkError", 0, e.message ?: "Unknown connection error."))
        }
    }

    override suspend fun getMovie(id: String): ApiResult<MovieDTO> {
        return safeGet("$BASE_URL/movies/$id")
    }

    override suspend fun getMoviesList(
        page: Int?,
        pageSize: Int?,
        sortBy: String?,
        sortOrder: String?,
        query: String?,
        genreId: Int?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?,
        maxRating: Float?,
    ): ApiResult<PaginatedResponseDTO<MovieListItemDTO>> {
        return safeGet("$BASE_URL/movies") {
            url {
                parameters.apply {
                    page?.let { append("page", it.toString()) }
                    pageSize?.let { append("page_size", it.toString()) }
                    sortBy?.let { append("sort_by", it) }
                    sortOrder?.let { append("sort_order", it) }
                    query?.let { append("query", it) }
                    genreId?.let { append("genre_id", it.toString()) }
                    minYear?.let { append("min_year", it.toString()) }
                    maxYear?.let { append("max_year", it.toString()) }
                    minRating?.let { append("min_rating", it.toString()) }
                }
            }
        }
    }

    override suspend fun getMovieCast(
        id: String,
        page: Int?,
        pageSize: Int?
    ): ApiResult<PaginatedResponseDTO<PersonSummaryDTO>> {
        return safeGet("$BASE_URL/movies/$id/cast") {
            url {
                parameters.apply {
                    page?.let { append("page", it.toString()) }
                    pageSize?.let { append("page_size", it.toString()) }
                }
            }
        }
    }

    override suspend fun getImages(id: String, type: String?): ApiResult<MovieImageDTO> {
        return safeGet("$BASE_URL/movies/$id/images") {
            url {
                type?.let { parameters.append("type", it) }
            }
        }
    }

    override suspend fun getVideos(id: String, type: String?): ApiResult<List<VideoDTO>> {
        return safeGet("$BASE_URL/movies/$id/videos") {
            url {
                type?.let { parameters.append("type", it) }
            }
        }
    }

    override suspend fun getCompanies(id: String): ApiResult<List<ProductionCompanyDTO>> {
        return safeGet("$BASE_URL/movies/$id/companies")
    }

    override suspend fun getPeoples(id: String): ApiResult<PersonDetailDTO> {
        return safeGet("$BASE_URL/people/$id")
    }

    override suspend fun getGenres(): ApiResult<List<GenreDTO>> {
        return safeGet("$BASE_URL/genres")
    }

    override suspend fun getCollection(id: Int): ApiResult<CollectionDetailDTO> {
        return safeGet("$BASE_URL/collections/$id")
    }

    override suspend fun getConfig(): ApiResult<List<ConfigEntryDTO>> {
        return safeGet("$BASE_URL/config")
    }

    override suspend fun getFavorites(): ApiResult<List<MovieListItemDTO>> {
        val token = getValidToken(dataStore)
        return safeGet("$BASE_URL/me/favorites") {
            token?.let { header("Authorization", "Bearer $it") }
        }
    }

    override suspend fun addFavorite(id: String): ApiResult<Unit> {
        return safePostUnit("$BASE_URL/me/favorites/$id")
    }

    override suspend fun deleteFavorite(id: String): ApiResult<Unit> {
        return safeDeleteUnit("$BASE_URL/me/favorites/$id")
    }

    override suspend fun getWatchlist(): ApiResult<List<MovieListItemDTO>> {
        val token = getValidToken(dataStore)
        return safeGet("$BASE_URL/me/watchlist") {
            token?.let { header("Authorization", "Bearer $it") }
        }
    }

    override suspend fun addWatchlist(id: String): ApiResult<Unit> {
        return safePostUnit("$BASE_URL/me/watchlist/$id")
    }

    override suspend fun deleteWatchlist(id: String): ApiResult<Unit> {
        return safeDeleteUnit("$BASE_URL/me/watchlist/$id")
    }
}
