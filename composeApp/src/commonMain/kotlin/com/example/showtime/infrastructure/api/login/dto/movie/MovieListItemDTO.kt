package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class MovieListItemDTO(
    val imdbId : String,
    val title : String,
    val year : Int?,
    val imdbRating : Float?,
    val imdbVotes : Int?,
    val posterPath : String?,
    val genres : List<GenreDTO>
) {
}