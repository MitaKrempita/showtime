package com.example.showtime.domain.model.movie

data class PersonSummary(
    val imdbId : String,
    val name : String,
    val professions : String?,
    val department : String?,
    val profilePath : String?
) {
}