package com.example.showtime.presentation.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieCollectionViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieCollectionState())
    val uiState: StateFlow<MovieCollectionState> = _uiState.asStateFlow()
    private val events = MutableSharedFlow<MovieCollectionIntent>(extraBufferCapacity = 32)

    init {
        viewModelScope.launch {
            events.collect { event ->
                handleEvent(event)
            }
        }
        viewModelScope.launch {
            repository.getFavoriteMovies().collect { movies ->
                _uiState.update { it.copy(favorites = movies) }
            }
        }
        viewModelScope.launch {
            repository.getWatchlistMovies().collect { movies ->
                _uiState.update { it.copy(watchlist = movies) }
            }
        }
        onEvent(MovieCollectionIntent.LoadData)
    }

    fun onEvent(event: MovieCollectionIntent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    private fun handleEvent(event: MovieCollectionIntent) {
        when (event) {
            MovieCollectionIntent.LoadData -> loadData()
            MovieCollectionIntent.Retry -> loadData()
            is MovieCollectionIntent.SelectType -> {
                _uiState.update { it.copy(selectedType = event.type) }
                loadData()
            }
            is MovieCollectionIntent.RemoveMovie -> removeMovie(event.movieId)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val config = _uiState.value.imageConfig ?: repository.getImageConfig()
                .flowOn(Dispatchers.Default)
                .first()
                .getOrNull()

            val result = if (_uiState.value.selectedType == MovieCollectionType.FAVORITES) {
                repository.syncFavorites()
            } else {
                repository.syncWatchlist()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    imageConfig = config,
                    errorMessage = if (result.isFailure && it.currentItems.isEmpty()) {
                        result.exceptionOrNull()?.message
                    } else {
                        null
                    }
                )
            }
        }
    }

    private fun removeMovie(movieId: String) {
        viewModelScope.launch {
            val result = if (_uiState.value.selectedType == MovieCollectionType.FAVORITES) {
                repository.toggleFavorite(movieId, false)
            } else {
                repository.toggleWatchlist(movieId, false)
            }
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
        }
    }
}
