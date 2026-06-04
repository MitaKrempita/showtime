package com.example.showtime.presentation.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.model.movie.Genre
import com.example.showtime.domain.repository.MovieRepository
import com.example.showtime.presentation.mainScreen.MovieFilters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FilterScreenViewModel(
    private val repository: MovieRepository,
    initialFilters: MovieFilters
) : ViewModel() {
    private val _uiState = MutableStateFlow(FilterScreenUiState.from(initialFilters))
    val uiState: StateFlow<FilterScreenUiState> = _uiState.asStateFlow()
    private val events = MutableSharedFlow<FilterScreenEvent>(extraBufferCapacity = 64)

    init {
        viewModelScope.launch {
            events.collect { event ->
                handleEvent(event)
            }
        }
        loadGenres()
    }

    fun onEvent(event: FilterScreenEvent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    private fun handleEvent(event: FilterScreenEvent) {
        when (event) {
            FilterScreenEvent.ClearAll -> _uiState.update {
                it.copy(
                    query = "",
                    selectedGenreId = null,
                    minYear = "",
                    maxYear = "",
                    minRating = 0f,
                    errorMessage = null
                )
            }
            is FilterScreenEvent.UpdateQuery -> _uiState.update { it.copy(query = event.value) }
            is FilterScreenEvent.UpdateGenre -> _uiState.update { it.copy(selectedGenreId = event.genreId) }
            is FilterScreenEvent.UpdateMinYear -> _uiState.update { it.copy(minYear = event.value) }
            is FilterScreenEvent.UpdateMaxYear -> _uiState.update { it.copy(maxYear = event.value) }
            is FilterScreenEvent.UpdateMinRating -> _uiState.update { it.copy(minRating = event.value) }
        }
    }

    fun currentFilters(): MovieFilters = _uiState.value.toFilters()

    private fun loadGenres() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingGenres = true, errorMessage = null) }
            val result = repository.getGenres()
                .flowOn(Dispatchers.Default)
                .first()

            result.fold(
                onSuccess = { genres ->
                    _uiState.update {
                        it.copy(
                            isLoadingGenres = false,
                            genres = genres,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoadingGenres = false,
                            errorMessage = throwable.message ?: "Failed to load genres."
                        )
                    }
                }
            )
        }
    }
}


