package com.example.showtime.infrastructure.api.login

import com.example.showtime.domain.model.movie.ImageConfig


object ImageUrlRoute {
    private const val BASE_URL = "https://image.tmdb.org/t/p"

    fun poster(config: ImageConfig, path: String?, size: String = "w342"): String? =
        path?.let { "${config.baseUrl}$size$it" }

    fun backdrop(config: ImageConfig, path: String?, size: String = "w780"): String? =
        path?.let { "${config.baseUrl}$size$it" }

    fun profile(config: ImageConfig, path: String?, size: String = "w185"): String? =
        path?.let { "${config.baseUrl}$size$it" }
}