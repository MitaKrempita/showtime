package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class VideoDTO(
    val key : String,
    val site : String,
    val name : String?,
    val type : String?,
    val official : Boolean,
    val publishedAt : String?
) {
}