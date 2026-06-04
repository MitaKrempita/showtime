package com.example.showtime.domain.model.movie

data class ImageConfig(
    val baseUrl: String,
    val posterSizes: List<String>,
    val backdropSizes: List<String>,
    val profileSizes: List<String>,
    val logoSizes: List<String>
)
