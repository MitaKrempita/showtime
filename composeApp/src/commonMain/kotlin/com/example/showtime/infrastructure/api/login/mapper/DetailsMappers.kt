package com.example.showtime.infrastructure.api.login.mapper

import com.example.showtime.domain.model.movie.Image
import com.example.showtime.domain.model.movie.ImageConfig
import com.example.showtime.domain.model.movie.MovieImages
import com.example.showtime.domain.model.movie.Video
import com.example.showtime.infrastructure.api.login.dto.movie.ConfigEntryDTO
import com.example.showtime.infrastructure.api.login.dto.movie.ImageDTO
import com.example.showtime.infrastructure.api.login.dto.movie.MovieImageDTO
import com.example.showtime.infrastructure.api.login.dto.movie.VideoDTO
import com.example.showtime.infrastructure.room.entity.ImageConfigEntity
import com.example.showtime.infrastructure.room.entity.MovieImageEntity
import com.example.showtime.infrastructure.room.entity.MovieVideoEntity

fun ImageDTO.toDomain(): Image = Image(
    filePath = filePath,
    width = width,
    height = height,
    voteAverage = voteAverage,
    language = language
)

fun MovieImageDTO.toDomain(): MovieImages = MovieImages(
    posters = posters.map { it.toDomain() },
    backdrops = backdrops.map { it.toDomain() },
    logos = logos.map { it.toDomain() }
)

fun VideoDTO.toDomain(): Video = Video(
    key = key,
    site = site,
    name = name,
    type = type,
    official = official,
    publishedAt = publishedAt
)

fun Image.toEntity(movieId: String, imageType: String): MovieImageEntity = MovieImageEntity(
    movieId = movieId,
    filePath = filePath,
    imageType = imageType,
    width = width,
    height = height,
    voteAverage = voteAverage,
    language = language
)

fun MovieImageEntity.toDomain(): Image = Image(
    filePath = filePath,
    width = width,
    height = height,
    voteAverage = voteAverage,
    language = language
)

fun Video.toEntity(movieId: String): MovieVideoEntity = MovieVideoEntity(
    movieId = movieId,
    key = key,
    site = site,
    name = name,
    type = type,
    official = official,
    publishedAt = publishedAt
)

fun MovieVideoEntity.toDomain(): Video = Video(
    key = key,
    site = site,
    name = name,
    type = type,
    official = official,
    publishedAt = publishedAt
)

fun ConfigEntryDTO.toEntity(): ImageConfigEntity = ImageConfigEntity(
    key = key,
    value = value
)

fun ImageConfigEntity.toDto(): ConfigEntryDTO = ConfigEntryDTO(
    key = key,
    value = value
)

fun List<ConfigEntryDTO>.toImageConfig(): ImageConfig {
    val map = associate { it.key to it.value }
    return ImageConfig(
        baseUrl = map["image_base_url"].orEmpty(),
        posterSizes = map["poster_sizes"].splitCsv(),
        backdropSizes = map["backdrop_sizes"].splitCsv(),
        profileSizes = map["profile_sizes"].splitCsv(),
        logoSizes = map["logo_sizes"].splitCsv()
    )
}

private fun String?.splitCsv(): List<String> =
    this?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
