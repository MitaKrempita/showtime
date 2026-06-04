package com.example.showtime.presentation.mainScreen

data class MovieFilters(
    val query: String = "",
    val genreId: Int? = null,
    val minYear: Int? = null,
    val maxYear: Int? = null,
    val minRating: Float? = null
) {
    val activeCount: Int
        get() = listOf(
            query.ifBlank { null },
            genreId,
            minYear,
            maxYear,
            minRating
        ).count { it != null }
}