package com.example.showtime.presentation.lists

sealed interface MovieCollectionIntent {
    data object LoadData : MovieCollectionIntent
    data object Retry : MovieCollectionIntent
    data class SelectType(val type: MovieCollectionType) : MovieCollectionIntent
    data class RemoveMovie(val movieId: String) : MovieCollectionIntent
}
