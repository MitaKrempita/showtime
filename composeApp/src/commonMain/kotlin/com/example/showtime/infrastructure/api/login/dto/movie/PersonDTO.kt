package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class PersonDTO(
    val imdbId : String,
    val tmdbId : Int?,
    val name : String,
    val birthYear : Int?,
    val deathYear : Int?,
    val professions : String?,
    val department : String?,
    val popularity : Float?,
    val profilePath : String?,
    val gender : Int?
) {
}