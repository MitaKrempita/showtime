package com.example.showtime.domain.model.movie

data class CollectionDetail(
    val collection : Collection,
    val movies : List<MovieListItem>
) {
}