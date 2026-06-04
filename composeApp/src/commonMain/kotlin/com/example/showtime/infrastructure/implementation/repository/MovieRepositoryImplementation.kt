package com.example.showtime.infrastructure.implementation.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.domain.model.movie.*
import com.example.showtime.infrastructure.api.login.mapper.*
import com.example.showtime.domain.api.MovieApi
import com.example.showtime.domain.repository.MovieRepository
import com.example.showtime.domain.repository.PagedResult
import com.example.showtime.infrastructure.datastore.clearAuth
import com.example.showtime.infrastructure.room.dao.MovieDao
import com.example.showtime.infrastructure.room.entity.FavoriteEntity
import com.example.showtime.infrastructure.room.entity.MovieCastEntity
import com.example.showtime.infrastructure.room.entity.WatchlistEntity
import com.example.showtime.infrastructure.network.ApiResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieRepositoryImplementation(
    private val api: MovieApi,
    private val movieDao: MovieDao,
    private val dataStore: DataStore<Preferences>
) : MovieRepository {
    private val cacheScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

    override fun getMoviesList(
        page: Int?,
        pageSize: Int?,
        sortBy: String?,
        sortOrder: String?,
        query: String?,
        genreId: Int?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?
    ): Flow<Result<PagedResult<MovieListItem>>> = flow {
        val selectedPage = page ?: 1
        val selectedPageSize = pageSize ?: 28
        var emittedLocal = false
        try {
            val local = movieDao.getCachedMoviesPage(
                query = query,
                genreId = genreId,
                minYear = minYear,
                maxYear = maxYear,
                minRating = minRating,
                sortBy = sortBy,
                sortOrder = sortOrder,
                limit = selectedPageSize,
                offset = (selectedPage - 1) * selectedPageSize
            )
            if (local.isNotEmpty()) {
                val localCount = movieDao.countCachedMovies(query, genreId, minYear, maxYear, minRating)
                val pagedLocal = PagedResult(
                    items = local.map { it.toMovieListItemDomain() },
                    page = selectedPage,
                    pageSize = selectedPageSize,
                    totalItems = localCount,
                    totalPages = (localCount + selectedPageSize - 1) / selectedPageSize
                )
                emit(Result.success(pagedLocal))
                emittedLocal = true
            }
        } catch (e: Exception) {
        }

        try {
            val apiResult = api.getMoviesList(
                page = page,
                pageSize = pageSize,
                sortBy = sortBy,
                sortOrder = sortOrder,
                query = query,
                genreId = genreId,
                minYear = minYear,
                maxYear = maxYear,
                minRating = minRating,
                maxRating = null
            ).getOrThrow()

            val entities = apiResult.items.map { it.toEntityWithRelations() }
            movieDao.insertMoviesWithRelations(entities)

            val updatedLocal = movieDao.getCachedMoviesPage(
                query = query,
                genreId = genreId,
                minYear = minYear,
                maxYear = maxYear,
                minRating = minRating,
                sortBy = sortBy,
                sortOrder = sortOrder,
                limit = selectedPageSize,
                offset = (selectedPage - 1) * selectedPageSize
            )
            val pagedUpdated = PagedResult(
                items = updatedLocal.map { it.toMovieListItemDomain() },
                page = apiResult.page,
                pageSize = apiResult.pageSize,
                totalItems = apiResult.totalItems,
                totalPages = apiResult.totalPages
            )
            emit(Result.success(pagedUpdated))

            warmUpMoviesNow(apiResult.items.map { it.imdbId })
            val warmedLocal = movieDao.getCachedMoviesPage(
                query = query,
                genreId = genreId,
                minYear = minYear,
                maxYear = maxYear,
                minRating = minRating,
                sortBy = sortBy,
                sortOrder = sortOrder,
                limit = selectedPageSize,
                offset = (selectedPage - 1) * selectedPageSize
            )
            emit(
                Result.success(
                    PagedResult(
                        items = warmedLocal.map { it.toMovieListItemDomain() },
                        page = apiResult.page,
                        pageSize = apiResult.pageSize,
                        totalItems = apiResult.totalItems,
                        totalPages = apiResult.totalPages
                    )
                )
            )
        } catch (e: Exception) {
            if (!emittedLocal) {
                emit(Result.failure(e))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getMovie(id: String): Flow<Result<Movie>> = channelFlow {
        val roomJob = launch {
            movieDao.getMovieById(id).collect { local ->
                if (local != null) {
                    send(Result.success(local.toMovieDomain()))
                }
            }
        }

        launch {
            try {
                val apiResult = api.getMovie(id).getOrThrow()
                movieDao.insertMovieWithRelations(apiResult.toEntityWithRelations())
                warmUpMovies(listOf(id))
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                if (movieDao.getMovieByIdOnce(id) == null) {
                    send(Result.failure(e))
                }
            }
        }

        awaitClose { roomJob.cancel() }
    }.flowOn(Dispatchers.IO)

    override fun getMovieCast(
        id: String,
        page: Int?,
        pageSize: Int?
    ): Flow<Result<PagedResult<PersonSummary>>> = channelFlow {
        val selectedPage = page ?: 1
        val selectedPageSize = pageSize ?: 20

        val roomJob = launch {
            movieDao.getMovieCast(id).collect { local ->
                if (local.isNotEmpty()) {
                    send(
                        Result.success(
                            PagedResult(
                                items = local.map { it.toDomain() },
                                page = selectedPage,
                                pageSize = selectedPageSize,
                                totalItems = local.size,
                                totalPages = 1
                            )
                        )
                    )
                }
            }
        }

        launch {
            try {
                val apiResult = api.getMovieCast(id, page, pageSize).getOrThrow()
                val peopleEntities = apiResult.items.map { it.toEntity() }
                movieDao.insertPeople(peopleEntities)
                movieDao.clearMovieCast(id)
                val castJunctions = peopleEntities.mapIndexed { index, person ->
                    MovieCastEntity(movieId = id, personId = person.imdbId, sortOrder = index)
                }
                movieDao.insertMovieCast(castJunctions)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                if (movieDao.getMovieCastOnce(id).isEmpty()) {
                    send(Result.failure(e))
                }
            }
        }

        awaitClose { roomJob.cancel() }
    }.flowOn(Dispatchers.IO)

    override fun getMovieImages(
        id: String,
        type: String?
    ): Flow<Result<MovieImages>> = flow {
        val local = movieDao.getMovieImagesOnce(id)
        if (local.isNotEmpty()) {
            emit(
                Result.success(
                    MovieImages(
                        posters = local.filter { it.imageType == "poster" }.map { it.toDomain() },
                        backdrops = local.filter { it.imageType == "backdrop" }.map { it.toDomain() },
                        logos = local.filter { it.imageType == "logo" }.map { it.toDomain() }
                    )
                )
            )
        }

        try {
            val apiResult = api.getImages(id, type).getOrThrow()
            val images = apiResult.toDomain()
            movieDao.replaceMovieImages(
                id,
                images.posters.map { it.toEntity(id, "poster") } +
                    images.backdrops.map { it.toEntity(id, "backdrop") } +
                    images.logos.map { it.toEntity(id, "logo") }
            )
            val updated = movieDao.getMovieImagesOnce(id)
            emit(
                Result.success(
                    MovieImages(
                        posters = updated.filter { it.imageType == "poster" }.map { it.toDomain() },
                        backdrops = updated.filter { it.imageType == "backdrop" }.map { it.toDomain() },
                        logos = updated.filter { it.imageType == "logo" }.map { it.toDomain() }
                    )
                )
            )
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            if (local.isEmpty()) emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getMovieVideos(
        id: String,
        type: String?
    ): Flow<Result<List<Video>>> = flow {
        val local = movieDao.getMovieVideosOnce(id)
        if (local.isNotEmpty()) {
            val localVideos = local.map { it.toDomain() }
            emit(Result.success(type?.let { t -> localVideos.filter { it.type == t } } ?: localVideos))
        }

        try {
            val apiResult = api.getVideos(id, type).getOrThrow()
            val videos = apiResult.map { it.toDomain() }
            movieDao.replaceMovieVideos(id, videos.map { it.toEntity(id) })
            val updated = movieDao.getMovieVideosOnce(id).map { it.toDomain() }
            emit(Result.success(type?.let { t -> updated.filter { it.type == t } } ?: updated))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            if (local.isEmpty()) emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getGenres(): Flow<Result<List<Genre>>> = flow {
        val local = try {
            movieDao.getGenresOnce()
        } catch (e: Exception) {
            emptyList()
        }

        if (local.isNotEmpty()) {
            emit(Result.success(local.map { it.toDomain() }))
        }

        try {
            val apiResult = api.getGenres().getOrThrow()
            val entities = apiResult.map { it.toEntity() }
            movieDao.insertGenres(entities)

            val updatedLocal = movieDao.getGenresOnce()
            emit(Result.success(updatedLocal.map { it.toDomain() }))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            if (local.isEmpty()) emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getImageConfig(): Flow<Result<ImageConfig>> = flow {
        val local = movieDao.getImageConfigOnce()
        if (local.isNotEmpty()) {
            emit(Result.success(local.map { it.toDto() }.toImageConfig()))
        }

        try {
            val apiResult = api.getConfig().getOrThrow()
            movieDao.insertImageConfig(apiResult.map { it.toEntity() })
            emit(Result.success(movieDao.getImageConfigOnce().map { it.toDto() }.toImageConfig()))
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            if (local.isEmpty()) emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getFavoriteMovies(): Flow<List<MovieListItem>> =
        movieDao.getFavoriteMovies().map { movies ->
            movies.map { it.toMovieListItemDomain() }
        }.flowOn(Dispatchers.IO)

    override fun getWatchlistMovies(): Flow<List<MovieListItem>> =
        movieDao.getWatchlistMovies().map { movies ->
            movies.map { it.toMovieListItemDomain() }
        }.flowOn(Dispatchers.IO)

    override suspend fun syncFavorites(): Result<Unit> = withContext(Dispatchers.IO) {
        when (val result = api.getFavorites()) {
            is ApiResult.Success -> {
                movieDao.replaceFavorites(result.data.map { it.toEntityWithRelations() })
                Result.success(Unit)
            }
            is ApiResult.Error -> {
                if (result.message.httpCode == 401) {
                    logoutLocally()
                }
                Result.failure(Exception(result.message.message))
            }
        }
    }

    override suspend fun syncWatchlist(): Result<Unit> = withContext(Dispatchers.IO) {
        when (val result = api.getWatchlist()) {
            is ApiResult.Success -> {
                movieDao.replaceWatchlist(result.data.map { it.toEntityWithRelations() })
                Result.success(Unit)
            }
            is ApiResult.Error -> {
                if (result.message.httpCode == 401) {
                    logoutLocally()
                }
                Result.failure(Exception(result.message.message))
            }
        }
    }

    override suspend fun toggleFavorite(movieId: String, isFavorite: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        if (isFavorite) {
            movieDao.insertFavorite(FavoriteEntity(movieId))
        } else {
            movieDao.deleteFavorite(FavoriteEntity(movieId))
        }

        val result = if (isFavorite) api.addFavorite(movieId) else api.deleteFavorite(movieId)
        when (result) {
            is ApiResult.Success -> Result.success(Unit)
            is ApiResult.Error -> {
                if (result.message.httpCode == 401) {
                    logoutLocally()
                } else if (isFavorite) {
                    movieDao.deleteFavorite(FavoriteEntity(movieId))
                } else {
                    movieDao.insertFavorite(FavoriteEntity(movieId))
                }
                Result.failure(Exception(result.message.message))
            }
        }
    }

    override suspend fun toggleWatchlist(movieId: String, isOnWatchlist: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        if (isOnWatchlist) {
            movieDao.insertWatchlist(WatchlistEntity(movieId))
        } else {
            movieDao.deleteWatchlist(WatchlistEntity(movieId))
        }

        val result = if (isOnWatchlist) api.addWatchlist(movieId) else api.deleteWatchlist(movieId)
        when (result) {
            is ApiResult.Success -> Result.success(Unit)
            is ApiResult.Error -> {
                if (result.message.httpCode == 401) {
                    logoutLocally()
                } else if (isOnWatchlist) {
                    movieDao.deleteWatchlist(WatchlistEntity(movieId))
                } else {
                    movieDao.insertWatchlist(WatchlistEntity(movieId))
                }
                Result.failure(Exception(result.message.message))
            }
        }
    }

    override suspend fun getFavoriteCount(): Int =
        withContext(Dispatchers.IO) { movieDao.getFavoriteMovies().first().size }

    override suspend fun getWatchlistCount(): Int =
        withContext(Dispatchers.IO) { movieDao.getWatchlistMovies().first().size }

    private fun warmUpMovies(ids: List<String>) {
        cacheScope.launch {
            warmUpMoviesNow(ids)
        }
    }

    private suspend fun warmUpMoviesNow(ids: List<String>) {
        ids.distinct().forEach { id ->
            warmUpMovie(id)
        }
    }

    private suspend fun warmUpMovie(id: String) {
        try {
            val local = movieDao.getMovieByIdOnce(id)
            if (local?.movie?.overview == null || local.movie.backdropPath == null || local.movie.runtime == null) {
                val movie = api.getMovie(id).getOrThrow()
                movieDao.insertMovieWithRelations(movie.toEntityWithRelations())
            }

            if (movieDao.getMovieImagesOnce(id).isEmpty()) {
                val images = api.getImages(id, null).getOrThrow().toDomain()
                movieDao.replaceMovieImages(
                    id,
                    images.posters.map { it.toEntity(id, "poster") } +
                        images.backdrops.map { it.toEntity(id, "backdrop") } +
                        images.logos.map { it.toEntity(id, "logo") }
                )
            }

            if (movieDao.getMovieCastOnce(id).isEmpty()) {
                val cast = api.getMovieCast(id, 1, 10).getOrThrow()
                val peopleEntities = cast.items.map { it.toEntity() }
                movieDao.insertPeople(peopleEntities)
                movieDao.clearMovieCast(id)
                movieDao.insertMovieCast(
                    peopleEntities.mapIndexed { index, person ->
                        MovieCastEntity(movieId = id, personId = person.imdbId, sortOrder = index)
                    }
                )
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        }
    }
}
