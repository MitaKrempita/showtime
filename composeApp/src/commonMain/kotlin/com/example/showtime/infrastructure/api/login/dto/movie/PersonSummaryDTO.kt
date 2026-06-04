package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class PersonSummaryDTO(
    val imdbId : String,
    val name : String,
    val professions : String?,
    val department : String?,
    val profilePath : String?
) {
}