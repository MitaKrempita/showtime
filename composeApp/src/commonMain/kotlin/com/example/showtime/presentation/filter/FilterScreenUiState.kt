package com.example.showtime.presentation.filter

import com.example.showtime.domain.model.movie.Genre
import com.example.showtime.presentation.mainScreen.MovieFilters

data class FilterScreenUiState(
    val query: String = "",
    val selectedGenreId: Int? = null,
    val minYear: String = "",
    val maxYear: String = "",
    val minRating: Float = 0f,
    val genres: List<Genre> = emptyList(),
    val isLoadingGenres: Boolean = false,
    val errorMessage: String? = null
) {
    fun toFilters(): MovieFilters = MovieFilters(
        query = query,
        genreId = selectedGenreId,
        minYear = minYear.toIntOrNull(),
        maxYear = maxYear.toIntOrNull(),
        minRating = if (minRating <= 0f) null else minRating
    )

    companion object {
        fun from(filters: MovieFilters): FilterScreenUiState = FilterScreenUiState(
            query = filters.query,
            selectedGenreId = filters.genreId,
            minYear = filters.minYear?.toString().orEmpty(),
            maxYear = filters.maxYear?.toString().orEmpty(),
            minRating = filters.minRating ?: 0f
        )
    }
}