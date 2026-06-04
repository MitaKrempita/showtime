package com.example.showtime.infrastructure.api.login.mapper

import com.example.showtime.domain.model.movie.Collection
import com.example.showtime.domain.model.movie.Genre
import com.example.showtime.domain.model.movie.Movie
import com.example.showtime.domain.model.movie.MovieListItem
import com.example.showtime.domain.model.movie.PersonSummary
import com.example.showtime.infrastructure.room.entity.*

fun GenreEntity.toDomain(): Genre = Genre(id = id, name = name)

fun CollectionEntity.toDomain(): Collection = Collection(
    id = id,
    name = name,
    posterPath = posterPath,
    backdropPath = backdropPath
)

fun PersonSummaryEntity.toDomain(): PersonSummary = PersonSummary(
    imdbId = imdbId,
    name = name,
    professions = professions,
    department = department,
    profilePath = profilePath
)

fun MovieWithRelations.toMovieListItemDomain(): MovieListItem = MovieListItem(
    imdbId = movie.imdbId,
    title = movie.title,
    year = movie.year,
    imdbRating = movie.imdbRating,
    imdbVotes = movie.imdbVotes,
    posterPath = movie.posterPath,
    genres = genres.map { it.toDomain() }
)

fun MovieWithRelations.toMovieDomain(): Movie = Movie(
    imdbId = movie.imdbId,
    tmdbId = movie.tmdbId,
    title = movie.title,
    originalTitle = movie.originalTitle,
    overview = movie.overview,
    tagline = movie.tagline,
    releaseDate = movie.releaseDate,
    year = movie.year,
    runtime = movie.runtime,
    budget = movie.budget,
    revenue = movie.revenue,
    languageCode = movie.languageCode,
    popularity = movie.popularity,
    imdbRating = movie.imdbRating,
    imdbVotes = movie.imdbVotes,
    tmdbRating = movie.tmdbRating,
    tmdbVotes = movie.tmdbVotes,
    posterPath = movie.posterPath,
    backdropPath = movie.backdropPath,
    homepage = movie.homepage,
    genres = genres.map { it.toDomain() },
    collection = collection?.toDomain(),
    isFavorite = isFavorite,
    isOnWatchlist = isOnWatchlist
)

fun Genre.toEntity(): GenreEntity = GenreEntity(id = id, name = name)

fun Collection.toEntity(): CollectionEntity = CollectionEntity(
    id = id,
    name = name,
    posterPath = posterPath,
    backdropPath = backdropPath
)
