package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class ImageDTO(
    val filePath : String,
    val width : Int?,
    val height : Int?,
    val voteAverage : Float?,
    val language : String?
) {
}
