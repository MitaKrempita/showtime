package com.example.showtime.infrastructure.api.login.mapper

import com.example.showtime.domain.model.movie.Collection
import com.example.showtime.domain.model.movie.Genre
import com.example.showtime.domain.model.movie.Movie
import com.example.showtime.domain.model.movie.MovieListItem
import com.example.showtime.infrastructure.api.login.dto.movie.CollectionDTO
import com.example.showtime.infrastructure.api.login.dto.movie.GenreDTO
import com.example.showtime.infrastructure.api.login.dto.movie.MovieDTO
import com.example.showtime.infrastructure.api.login.dto.movie.MovieListItemDTO

fun GenreDTO.toDomain(): Genre = Genre(
    id = id,
    name = name
)

fun CollectionDTO.toDomain(): Collection = Collection(
    id = id,
    name = name,
    posterPath = posterPath,
    backdropPath = backdropPath
)

fun MovieListItemDTO.toDomain(): MovieListItem = MovieListItem(
    imdbId = imdbId,
    title = title,
    year = year,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    posterPath = posterPath,
    genres = genres.map { it.toDomain() }
)

fun MovieDTO.toDomain(): Movie = Movie(
    imdbId = imdbId,
    tmdbId = tmdbId,
    title = title,
    originalTitle = originalTitle,
    overview = overview,
    tagline = tagline,
    releaseDate = releaseDate,
    year = year,
    runtime = runtime,
    budget = budget,
    revenue = revenue,
    languageCode = languageCode,
    popularity = popularity,
    imdbRating = imdbRating,
    imdbVotes = imdbVotes,
    tmdbRating = tmdbRating,
    tmdbVotes = tmdbVotes,
    posterPath = posterPath,
    backdropPath = backdropPath,
    homepage = homepage,
    genres = genres.map { it.toDomain() },
    collection = collection?.toDomain()
)

