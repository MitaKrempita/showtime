package com.example.showtime.presentation.filter

sealed interface FilterScreenEvent {
    data class UpdateQuery(val value: String) : FilterScreenEvent
    data class UpdateGenre(val genreId: Int?) : FilterScreenEvent
    data class UpdateMinYear(val value: String) : FilterScreenEvent
    data class UpdateMaxYear(val value: String) : FilterScreenEvent
    data class UpdateMinRating(val value: Float) : FilterScreenEvent
    data object ClearAll : FilterScreenEvent
}