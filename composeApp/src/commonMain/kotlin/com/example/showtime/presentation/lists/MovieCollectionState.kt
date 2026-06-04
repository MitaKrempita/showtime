package com.example.showtime.presentation.lists

import com.example.showtime.domain.model.movie.ImageConfig
import com.example.showtime.domain.model.movie.MovieListItem

data class MovieCollectionState(
    val selectedType: MovieCollectionType = MovieCollectionType.FAVORITES,
    val favorites: List<MovieListItem> = emptyList(),
    val watchlist: List<MovieListItem> = emptyList(),
    val imageConfig: ImageConfig? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val currentItems: List<MovieListItem>
        get() = if (selectedType == MovieCollectionType.FAVORITES) favorites else watchlist
}
