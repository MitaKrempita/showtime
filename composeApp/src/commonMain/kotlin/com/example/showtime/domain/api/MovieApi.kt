package com.example.showtime.domain.api

import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.api.login.dto.movie.*


interface MovieApi {
    suspend fun getMovie(id: String): ApiResult<MovieDTO>
    suspend fun getMoviesList(
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
    ): ApiResult<PaginatedResponseDTO<MovieListItemDTO>>

    suspend fun getMovieCast(
        id: String,
        page: Int?,
        pageSize: Int?
    ): ApiResult<PaginatedResponseDTO<PersonSummaryDTO>>

    suspend fun getImages(id: String, type: String?): ApiResult<MovieImageDTO>
    suspend fun getVideos(id: String, type: String?): ApiResult<List<VideoDTO>>
    suspend fun getCompanies(id: String): ApiResult<List<ProductionCompanyDTO>>
    suspend fun getPeoples(id: String): ApiResult<PersonDetailDTO>
    suspend fun getGenres(): ApiResult<List<GenreDTO>>
    suspend fun getCollection(id: Int): ApiResult<CollectionDetailDTO>
    suspend fun getConfig(): ApiResult<List<ConfigEntryDTO>>
    suspend fun getFavorites(): ApiResult<List<MovieListItemDTO>>
    suspend fun addFavorite(id: String): ApiResult<Unit>
    suspend fun deleteFavorite(id: String): ApiResult<Unit>
    suspend fun getWatchlist(): ApiResult<List<MovieListItemDTO>>
    suspend fun addWatchlist(id: String): ApiResult<Unit>
    suspend fun deleteWatchlist(id: String): ApiResult<Unit>
}
