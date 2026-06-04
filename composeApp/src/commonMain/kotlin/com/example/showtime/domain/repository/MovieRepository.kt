package com.example.showtime.domain.repository

import com.example.showtime.domain.model.movie.Genre
import com.example.showtime.domain.model.movie.ImageConfig
import com.example.showtime.domain.model.movie.Movie
import com.example.showtime.domain.model.movie.MovieImages
import com.example.showtime.domain.model.movie.MovieListItem
import com.example.showtime.domain.model.movie.PersonSummary
import com.example.showtime.domain.model.movie.Video
import kotlinx.coroutines.flow.Flow

data class PagedResult<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)
/**
 * Repozitorijum za pristup podacima o filmovima.
 *
 *
 * Sve metode vracaju [Flow] koji emituje [Result].
 **/
interface MovieRepository {
    fun getMoviesList(
        page: Int? = null,
        pageSize: Int? = null,
        sortBy: String? = null,
        sortOrder: String? = null,
        query: String? = null,
        genreId: Int? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null
    ): Flow<Result<PagedResult<MovieListItem>>>

    fun getMovie(id: String): Flow<Result<Movie>>

    fun getMovieCast(
        id: String,
        page: Int? = null,
        pageSize: Int? = null
    ): Flow<Result<PagedResult<PersonSummary>>>

    fun getMovieImages(
        id: String,
        type: String? = null
    ): Flow<Result<MovieImages>>

    fun getMovieVideos(
        id: String,
        type: String? = null
    ): Flow<Result<List<Video>>>

    fun getGenres(): Flow<Result<List<Genre>>>

    fun getImageConfig(): Flow<Result<ImageConfig>>

    fun getFavoriteMovies(): Flow<List<MovieListItem>>
    fun getWatchlistMovies(): Flow<List<MovieListItem>>
    suspend fun syncFavorites(): Result<Unit>
    suspend fun syncWatchlist(): Result<Unit>
    suspend fun toggleFavorite(movieId: String, isFavorite: Boolean): Result<Unit>
    suspend fun toggleWatchlist(movieId: String, isOnWatchlist: Boolean): Result<Unit>
    suspend fun getFavoriteCount(): Int
    suspend fun getWatchlistCount(): Int
}
