package com.example.showtime.domain.model.movie

data class MovieImages(
    val posters : List<Image>,
    val backdrops : List<Image>,
    val logos : List<Image>
) {
}