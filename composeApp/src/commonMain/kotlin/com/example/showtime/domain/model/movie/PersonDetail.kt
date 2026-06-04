package com.example.showtime.domain.model.movie

data class PersonDetail(
    val person : Person,
    val movies : List<MovieListItem>
) {
}