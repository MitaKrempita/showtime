package com.example.showtime.infrastructure.api.login.mapper

import com.example.showtime.infrastructure.api.login.dto.movie.CollectionDTO
import com.example.showtime.infrastructure.api.login.dto.movie.GenreDTO
import com.example.showtime.infrastructure.api.login.dto.movie.MovieDTO
import com.example.showtime.infrastructure.api.login.dto.movie.MovieListItemDTO
import com.example.showtime.infrastructure.api.login.dto.movie.PersonSummaryDTO
import com.example.showtime.infrastructure.room.entity.CollectionEntity
import com.example.showtime.infrastructure.room.entity.GenreEntity
import com.example.showtime.infrastructure.room.entity.MovieEntity
import com.example.showtime.infrastructure.room.entity.MovieWithRelations
import com.example.showtime.infrastructure.room.entity.PersonSummaryEntity

fun GenreDTO.toEntity(): GenreEntity = GenreEntity(
    id = id,
    name = name
)

fun CollectionDTO.toEntity(): CollectionEntity = CollectionEntity(
    id = id,
    name = name,
    posterPath = posterPath,
    backdropPath = backdropPath
)

fun PersonSummaryDTO.toEntity(): PersonSummaryEntity = PersonSummaryEntity(
    imdbId = imdbId,
    name = name,
    professions = professions,
    department = department,
    profilePath = profilePath
)

fun MovieListItemDTO.toEntityWithRelations(): MovieWithRelations = MovieWithRelations(
    movie = MovieEntity(
        imdbId = imdbId,
        collectionId = null,
        tmdbId = null,
        title = title,
        originalTitle = null,
        overview = null,
        tagline = null,
        releaseDate = null,
        year = year,
        runtime = null,
        budget = null,
        revenue = null,
        languageCode = null,
        popularity = null,
        imdbRating = imdbRating,
        imdbVotes = imdbVotes,
        tmdbRating = null,
        tmdbVotes = null,
        posterPath = posterPath,
        backdropPath = null,
        homepage = null
    ),
    collection = null,
    genres = genres.map { it.toEntity() }
)

fun MovieDTO.toEntityWithRelations(): MovieWithRelations = MovieWithRelations(
    movie = MovieEntity(
        imdbId = imdbId,
        collectionId = collection?.id,
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
        homepage = homepage
    ),
    collection = collection?.toEntity(),
    genres = genres.map { it.toEntity() }
)