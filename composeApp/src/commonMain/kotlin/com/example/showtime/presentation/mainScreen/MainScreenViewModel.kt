package com.example.showtime.presentation.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * View Model za glavni ekran aplikacije
 *
 * upravlja UI-em
 * @param repository izvor podataka.
 * **/
class MainScreenViewModel(
    private val repository: MovieRepository? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()
    private val events = MutableSharedFlow<MainScreenIntent>(extraBufferCapacity = 64)

    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            events.collect { event ->
                handleEvent(event)
            }
        }
        if (repository != null) {
            onEvent(MainScreenIntent.LoadMovies)
        }
    }

    fun onEvent(event: MainScreenIntent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    private fun handleEvent(event: MainScreenIntent) {
        when (event) {
            MainScreenIntent.LoadMovies -> loadMovies()
            MainScreenIntent.LoadNextPage -> loadMovies(loadNextPage = true)
            MainScreenIntent.Retry -> loadMovies()
            MainScreenIntent.ToggleTheme -> toggleTheme()
            MainScreenIntent.ToggleSortPicker -> toggleSortPicker()
            MainScreenIntent.ToggleSortOrderPicker -> toggleSortOrderPicker()
            is MainScreenIntent.ChangeSort -> changeSort(event.sortOption)
            is MainScreenIntent.ChangeSortOrder -> changeSortOrder(event.sortOrder)
            is MainScreenIntent.ApplyFilters -> applyFilters(event.filters)
            MainScreenIntent.ClearFilters -> applyFilters(MovieFilters())
        }
    }

    private fun loadMovies(loadNextPage: Boolean = false) {
        val currentRepository = repository
        if (currentRepository == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "MovieRepository is not connected yet."
                )
            }
            return
        }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val currentState = _uiState.value
            if (loadNextPage && !currentState.canLoadMore) return@launch
            val pageToLoad = if (loadNextPage) currentState.currentPage + 1 else 1
            _uiState.update {
                if (loadNextPage) {
                    it.copy(isLoadingMore = true, errorMessage = null)
                } else {
                    it.copy(isLoading = true, isLoadingMore = false, errorMessage = null, currentPage = 1)
                }
            }
            val imageConfig = currentState.imageConfig ?: currentRepository.getImageConfig()
                .flowOn(Dispatchers.IO)
                .first()
                .getOrNull()

            currentRepository.getMoviesList(
                page = pageToLoad,
                pageSize = 28,
                sortBy = currentState.sortOption.apiValue,
                sortOrder = currentState.sortOrder.apiValue,
                query = currentState.filters.query.ifBlank { null },
                genreId = currentState.filters.genreId,
                minYear = currentState.filters.minYear,
                maxYear = currentState.filters.maxYear,
                minRating = currentState.filters.minRating
            )
                .flowOn(Dispatchers.Default)
                .collect { result ->
                    result.fold(
                        onSuccess = { page ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoadingMore = false,
                                    movies = if (loadNextPage) {
                                        (it.movies + page.items).distinctBy { movie -> movie.imdbId }
                                    } else {
                                        page.items
                                    },
                                    totalMovies = page.totalItems,
                                    currentPage = page.page,
                                    totalPages = page.totalPages,
                                    imageConfig = imageConfig,
                                    errorMessage = null
                                )
                            }
                        },
                        onFailure = { throwable ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoadingMore = false,
                                    movies = if (loadNextPage) it.movies else emptyList(),
                                    totalMovies = if (loadNextPage) it.totalMovies else 0,
                                    errorMessage = throwable.message ?: "Failed to load movies."
                                )
                            }
                        }
                    )
                }
        }
    }

    private fun changeSort(sortOption: MovieSortOption) {
        _uiState.update {
            it.copy(
                sortOption = sortOption,
                isSortPickerOpen = false,
                isSortOrderPickerOpen = false
            )
        }
        loadMovies()
    }

    private fun changeSortOrder(sortOrder: SortOrder) {
        _uiState.update {
            it.copy(
                sortOrder = sortOrder,
                isSortPickerOpen = false,
                isSortOrderPickerOpen = false
            )
        }
        loadMovies()
    }

    private fun applyFilters(filters: MovieFilters) {
        _uiState.update { it.copy(filters = filters) }
        loadMovies()
    }

    private fun toggleTheme() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    private fun toggleSortPicker() {
        _uiState.update {
            it.copy(
                isSortPickerOpen = !it.isSortPickerOpen,
                isSortOrderPickerOpen = false
            )
        }
    }

    private fun toggleSortOrderPicker() {
        _uiState.update {
            it.copy(
                isSortOrderPickerOpen = !it.isSortOrderPickerOpen,
                isSortPickerOpen = false
            )
        }
    }


}
