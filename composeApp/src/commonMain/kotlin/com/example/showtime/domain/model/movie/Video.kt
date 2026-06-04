package com.example.showtime.domain.model.movie

data class Video(
    val key : String,
    val site : String,
    val name : String?,
    val type : String?,
    val official : Boolean,
    val publishedAt : String?
) {
}