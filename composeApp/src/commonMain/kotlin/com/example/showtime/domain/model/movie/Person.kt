package com.example.showtime.domain.model.movie

data class Person(
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