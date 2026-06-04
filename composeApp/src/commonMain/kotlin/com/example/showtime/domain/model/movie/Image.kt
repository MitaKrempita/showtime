package com.example.showtime.domain.model.movie

data class Image(
    val filePath : String,
    val width : Int?,
    val height : Int?,
    val voteAverage : Float?,
    val language : String?
) {
}
