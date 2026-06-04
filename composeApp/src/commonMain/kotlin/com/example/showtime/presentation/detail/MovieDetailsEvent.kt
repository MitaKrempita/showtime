package com.example.showtime.presentation.detail

sealed interface MovieDetailsEvent {
    data object Retry : MovieDetailsEvent
    data object ToggleFavorite : MovieDetailsEvent
    data object ToggleWatchlist : MovieDetailsEvent
}
