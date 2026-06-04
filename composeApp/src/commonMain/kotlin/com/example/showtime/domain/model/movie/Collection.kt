package com.example.showtime.domain.model.movie

data class Collection(
    val id : Int,
    val name : String,
    val posterPath : String?,
    val backdropPath : String?
) {
}