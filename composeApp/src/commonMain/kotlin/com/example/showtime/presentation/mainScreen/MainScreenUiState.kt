package com.example.showtime.presentation.mainScreen

import androidx.compose.ui.graphics.Color
import com.example.showtime.domain.model.movie.ImageConfig
import com.example.showtime.domain.model.movie.MovieListItem
import com.example.showtime.presentation.mainScreen.MainScreenViewModel.*


data class MainScreenUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val movies: List<MovieListItem> = emptyList(),
    val totalMovies: Int = 0,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val errorMessage: String? = null,
    val sortOption: MovieSortOption = MovieSortOption.Rating,
    val sortOrder: SortOrder = SortOrder.Descending,
    val filters: MovieFilters = MovieFilters(),
    val imageConfig: ImageConfig? = null,
    val isSortPickerOpen: Boolean = false,
    val isSortOrderPickerOpen: Boolean = false,
    val isDarkMode: Boolean = true
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && movies.isEmpty()

    val canLoadMore: Boolean
        get() = !isLoading && !isLoadingMore && currentPage < totalPages

    val activeFilterCount: Int
        get() = filters.activeCount

    val primary: Color
        get() = if (isDarkMode) Color(220, 220, 220, 255) else Color(48, 49, 56, 255)

    val onPrimary: Color
        get() = if (isDarkMode) Color(255, 255, 255, 255) else Color(15, 15, 15, 255)

    val secondary: Color
        get() = if (isDarkMode) Color(203, 3, 3, 255) else Color(180, 0, 0, 255)

    val onSecondary: Color
        get() = if (isDarkMode) Color.White else Color.Black

    val contentTextColor: Color
        get() = if (isDarkMode) Color(22, 24, 29, 255) else Color.White

    val mutedTextColor: Color
        get() = contentTextColor.copy(alpha = 0.72f)
}
