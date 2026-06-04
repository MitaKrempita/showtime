package com.example.showtime.infrastructure.room.dao

import androidx.room.*
import com.example.showtime.infrastructure.room.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieGenres(movieGenres: List<MovieGenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeople(people: List<PersonSummaryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieCast(cast: List<MovieCastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieImages(images: List<MovieImageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieVideos(videos: List<MovieVideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageConfig(config: List<ImageConfigEntity>)

    @Query("DELETE FROM movie_genre_junction WHERE imdbId = :movieId")
    suspend fun clearMovieGenres(movieId: String)

    @Query("DELETE FROM movie_cast_junction WHERE movieId = :movieId")
    suspend fun clearMovieCast(movieId: String)

    @Query("DELETE FROM movie_images WHERE movieId = :movieId")
    suspend fun clearMovieImages(movieId: String)

    @Query("DELETE FROM movie_videos WHERE movieId = :movieId")
    suspend fun clearMovieVideos(movieId: String)

    @Query("UPDATE movies SET posterPath = COALESCE(posterPath, :posterPath), backdropPath = COALESCE(backdropPath, :backdropPath) WHERE imdbId = :movieId")
    suspend fun updateMovieImageFallbacks(movieId: String, posterPath: String?, backdropPath: String?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(watchlist: WatchlistEntity)

    @Delete
    suspend fun deleteWatchlist(watchlist: WatchlistEntity)

    @Transaction
    suspend fun insertMovieWithRelations(item: MovieWithRelations) {
        item.collection?.let { insertCollection(it) }
        insertGenres(item.genres)
        val old = getMovieByIdOnce(item.movie.imdbId)?.movie
        val movie = if (old == null) item.movie else item.movie.copy(
            collectionId = item.movie.collectionId ?: old.collectionId,
            tmdbId = item.movie.tmdbId ?: old.tmdbId,
            originalTitle = item.movie.originalTitle ?: old.originalTitle,
            overview = item.movie.overview ?: old.overview,
            tagline = item.movie.tagline ?: old.tagline,
            releaseDate = item.movie.releaseDate ?: old.releaseDate,
            year = item.movie.year ?: old.year,
            runtime = item.movie.runtime ?: old.runtime,
            budget = item.movie.budget ?: old.budget,
            revenue = item.movie.revenue ?: old.revenue,
            languageCode = item.movie.languageCode ?: old.languageCode,
            popularity = item.movie.popularity ?: old.popularity,
            imdbRating = item.movie.imdbRating ?: old.imdbRating,
            imdbVotes = item.movie.imdbVotes ?: old.imdbVotes,
            tmdbRating = item.movie.tmdbRating ?: old.tmdbRating,
            tmdbVotes = item.movie.tmdbVotes ?: old.tmdbVotes,
            posterPath = item.movie.posterPath ?: old.posterPath,
            backdropPath = item.movie.backdropPath ?: old.backdropPath,
            homepage = item.movie.homepage ?: old.homepage
        )
        insertMovie(movie)
        clearMovieGenres(item.movie.imdbId)

        val junctions = item.genres.map { MovieGenreEntity(movie.imdbId, it.id) }
        insertMovieGenres(junctions)
    }

    @Transaction
    suspend fun replaceFavorites(items: List<MovieWithRelations>) {
        clearFavorites()
        insertMoviesWithRelations(items)
        items.forEach { insertFavorite(FavoriteEntity(it.movie.imdbId)) }
    }

    @Transaction
    suspend fun replaceWatchlist(items: List<MovieWithRelations>) {
        clearWatchlist()
        insertMoviesWithRelations(items)
        items.forEach { insertWatchlist(WatchlistEntity(it.movie.imdbId)) }
    }

    @Transaction
    suspend fun replaceMovieImages(movieId: String, images: List<MovieImageEntity>) {
        clearMovieImages(movieId)
        insertMovieImages(images)
        updateMovieImageFallbacks(
            movieId = movieId,
            posterPath = images
                .filter { it.imageType == "poster" }
                .maxByOrNull { it.voteAverage ?: 0f }
                ?.filePath,
            backdropPath = images
                .filter { it.imageType == "backdrop" }
                .maxByOrNull { it.voteAverage ?: 0f }
                ?.filePath
        )
    }

    @Transaction
    suspend fun replaceMovieVideos(movieId: String, videos: List<MovieVideoEntity>) {
        clearMovieVideos(movieId)
        insertMovieVideos(videos)
    }

    @Transaction
    suspend fun insertMoviesWithRelations(items: List<MovieWithRelations>) {
        items.forEach { insertMovieWithRelations(it) }
    }

    @Transaction
    @Query("SELECT * FROM movies WHERE imdbId = :id")
    fun getMovieById(id: String): Flow<MovieWithRelations?>

    @Transaction
    @Query("SELECT * FROM movies WHERE imdbId = :id")
    suspend fun getMovieByIdOnce(id: String): MovieWithRelations?

    @Transaction
    @Query("SELECT * FROM movies")
    suspend fun getCachedMovies(): List<MovieWithRelations>

    @Transaction
    @Query("SELECT * FROM movies WHERE (posterPath IS NOT NULL AND posterPath != '') OR (backdropPath IS NOT NULL AND backdropPath != '')")
    suspend fun getQuizReadyMovies(): List<MovieWithRelations>

    @Query("SELECT COUNT(*) FROM movies WHERE (posterPath IS NOT NULL AND posterPath != '') OR (backdropPath IS NOT NULL AND backdropPath != '')")
    suspend fun countQuizReadyMovies(): Int

    @Transaction
    @Query(
        """
        SELECT * FROM movies
        WHERE (:query IS NULL OR title LIKE '%' || :query || '%' OR originalTitle LIKE '%' || :query || '%')
        AND (:genreId IS NULL OR imdbId IN (SELECT imdbId FROM movie_genre_junction WHERE genreId = :genreId))
        AND (:minYear IS NULL OR year >= :minYear)
        AND (:maxYear IS NULL OR year <= :maxYear)
        AND (:minRating IS NULL OR imdbRating >= :minRating)
        ORDER BY
            CASE WHEN :sortBy = 'title' AND :sortOrder = 'asc' THEN title END COLLATE NOCASE ASC,
            CASE WHEN :sortBy = 'title' AND :sortOrder = 'desc' THEN title END COLLATE NOCASE DESC,
            CASE WHEN :sortBy = 'year' AND :sortOrder = 'asc' THEN year END ASC,
            CASE WHEN :sortBy = 'year' AND :sortOrder = 'desc' THEN year END DESC,
            CASE WHEN :sortBy = 'imdb_rating' AND :sortOrder = 'asc' THEN imdbRating END ASC,
            CASE WHEN :sortBy = 'imdb_rating' AND :sortOrder = 'desc' THEN imdbRating END DESC,
            CASE WHEN :sortBy = 'popularity' AND :sortOrder = 'asc' THEN popularity END ASC,
            CASE WHEN :sortBy = 'popularity' AND :sortOrder = 'desc' THEN popularity END DESC,
            imdbRating DESC
        LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getCachedMoviesPage(
        query: String?,
        genreId: Int?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?,
        sortBy: String?,
        sortOrder: String?,
        limit: Int,
        offset: Int
    ): List<MovieWithRelations>

    @Query(
        """
        SELECT COUNT(*) FROM movies
        WHERE (:query IS NULL OR title LIKE '%' || :query || '%' OR originalTitle LIKE '%' || :query || '%')
        AND (:genreId IS NULL OR imdbId IN (SELECT imdbId FROM movie_genre_junction WHERE genreId = :genreId))
        AND (:minYear IS NULL OR year >= :minYear)
        AND (:maxYear IS NULL OR year <= :maxYear)
        AND (:minRating IS NULL OR imdbRating >= :minRating)
        """
    )
    suspend fun countCachedMovies(
        query: String?,
        genreId: Int?,
        minYear: Int?,
        maxYear: Int?,
        minRating: Float?
    ): Int

    @Transaction
    @Query("SELECT * FROM movies WHERE imdbId IN (SELECT imdbId FROM favorites)")
    fun getFavoriteMovies(): Flow<List<MovieWithRelations>>

    @Transaction
    @Query("SELECT * FROM movies WHERE imdbId IN (SELECT imdbId FROM watchlist)")
    fun getWatchlistMovies(): Flow<List<MovieWithRelations>>

    @Transaction
    @Query(
        """
        SELECT people.* FROM people
        INNER JOIN movie_cast_junction ON people.imdbId = movie_cast_junction.personId
        WHERE movie_cast_junction.movieId = :movieId
        ORDER BY movie_cast_junction.sortOrder ASC
        """
    )
    fun getMovieCast(movieId: String): Flow<List<PersonSummaryEntity>>

    @Transaction
    @Query(
        """
        SELECT people.* FROM people
        INNER JOIN movie_cast_junction ON people.imdbId = movie_cast_junction.personId
        WHERE movie_cast_junction.movieId = :movieId
        ORDER BY movie_cast_junction.sortOrder ASC
        """
    )
    suspend fun getMovieCastOnce(movieId: String): List<PersonSummaryEntity>

    @Query("SELECT * FROM movie_images WHERE movieId = :movieId ORDER BY voteAverage DESC")
    suspend fun getMovieImagesOnce(movieId: String): List<MovieImageEntity>

    @Query("SELECT * FROM movie_videos WHERE movieId = :movieId")
    suspend fun getMovieVideosOnce(movieId: String): List<MovieVideoEntity>

    @Query("SELECT * FROM image_config")
    suspend fun getImageConfigOnce(): List<ImageConfigEntity>

    @Query("DELETE FROM movies")
    suspend fun clearMovies()

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()

    @Query("DELETE FROM watchlist")
    suspend fun clearWatchlist()

    @Query("SELECT * FROM genres ORDER BY name ASC")
    suspend fun getGenresOnce(): List<GenreEntity>
}
