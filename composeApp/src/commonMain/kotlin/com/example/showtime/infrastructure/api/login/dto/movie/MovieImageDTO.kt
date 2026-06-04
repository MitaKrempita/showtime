package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class MovieImageDTO(
    val posters : List<ImageDTO>,
    val backdrops : List<ImageDTO>,
    val logos : List<ImageDTO>
) {
}