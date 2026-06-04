package com.example.showtime.presentation.mainScreen

import com.example.showtime.presentation.mainScreen.MainScreenViewModel.*

sealed interface MainScreenIntent {
    data object LoadMovies : MainScreenIntent
    data object LoadNextPage : MainScreenIntent
    data object Retry : MainScreenIntent
    data object ToggleTheme : MainScreenIntent
    data object ToggleSortPicker : MainScreenIntent
    data object ToggleSortOrderPicker : MainScreenIntent
    data object ClearFilters : MainScreenIntent
    data class ChangeSort(val sortOption: MovieSortOption) : MainScreenIntent
    data class ChangeSortOrder(val sortOrder: SortOrder) : MainScreenIntent
    data class ApplyFilters(val filters: MovieFilters) : MainScreenIntent
}
