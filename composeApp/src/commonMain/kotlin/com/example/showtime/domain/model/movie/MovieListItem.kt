package com.example.showtime.domain.model.movie

data class MovieListItem (
    val imdbId : String,
    val title : String,
    val year : Int?,
    val imdbRating : Float?,
    val imdbVotes : Int?,
    val posterPath : String?,
    val genres : List<Genre>,
)
{

}