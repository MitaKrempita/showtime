package com.example.showtime.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.model.movie.Image
import com.example.showtime.domain.model.movie.ImageConfig
import com.example.showtime.domain.model.movie.Movie
import com.example.showtime.domain.model.movie.PersonSummary
import com.example.showtime.domain.model.movie.Video
import com.example.showtime.domain.repository.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val movieId: String,
    private val repository: MovieRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()
    private val events = MutableSharedFlow<MovieDetailsEvent>(extraBufferCapacity = 16)
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            events.collect { event ->
                handleEvent(event)
            }
        }
        loadDetails()
    }

    fun onEvent(event: MovieDetailsEvent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    private fun handleEvent(event: MovieDetailsEvent) {
        when (event) {
            MovieDetailsEvent.Retry -> loadDetails()
            MovieDetailsEvent.ToggleFavorite -> toggleFavorite()
            MovieDetailsEvent.ToggleWatchlist -> toggleWatchlist()
        }
    }

    private fun loadDetails() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            launch {
                repository.getImageConfig().collect { result ->
                    result.onSuccess { config ->
                        _uiState.update { it.copy(imageConfig = config) }
                    }
                }
            }

            launch {
                repository.syncFavorites()
                repository.syncWatchlist()
            }

            launch {
                repository.getMovie(movieId).collect { result ->
                    result.onSuccess { movie ->
                        _uiState.update { it.copy(isLoading = false, movie = movie, errorMessage = null) }
                    }
                    result.onFailure { error ->
                        if (_uiState.value.movie == null) {
                            _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                        }
                    }
                }
            }

            launch {
                repository.getMovieCast(movieId, 1, 10).collect { result ->
                    result.onSuccess { paged ->
                        _uiState.update { it.copy(cast = paged.items) }
                    }
                }
            }

            launch {
                repository.getMovieImages(movieId, null).collect { result ->
                    result.onSuccess { images ->
                        _uiState.update { it.copy(backdrops = images.backdrops.distinctBy { it.filePath }) }
                    }
                }
            }

            launch {
                repository.getMovieVideos(movieId, "Trailer").collect { result ->
                    result.onSuccess { videos ->
                        _uiState.update { it.copy(trailer = selectTrailer(videos)) }
                    }
                }
            }
        }
    }

    private fun selectTrailer(videos: List<Video>): Video? =
        videos.firstOrNull { it.site.equals("YouTube", ignoreCase = true) && it.official }
            ?: videos.firstOrNull { it.site.equals("YouTube", ignoreCase = true) }
            ?: videos.firstOrNull()

    private fun toggleFavorite() {
        val movie = _uiState.value.movie ?: return
        val newValue = !movie.isFavorite
        _uiState.update {
            it.copy(
                movie = movie.copy(isFavorite = newValue),
                actionMessage = null
            )
        }
        viewModelScope.launch {
            repository.toggleFavorite(movie.imdbId, newValue)
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            movie = it.movie?.copy(isFavorite = !newValue),
                            actionMessage = error.message ?: "Failed to update favorite."
                        )
                    }
                }
        }
    }

    private fun toggleWatchlist() {
        val movie = _uiState.value.movie ?: return
        val newValue = !movie.isOnWatchlist
        _uiState.update {
            it.copy(
                movie = movie.copy(isOnWatchlist = newValue),
                actionMessage = null
            )
        }
        viewModelScope.launch {
            repository.toggleWatchlist(movie.imdbId, newValue)
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            movie = it.movie?.copy(isOnWatchlist = !newValue),
                            actionMessage = error.message ?: "Failed to update watchlist."
                        )
                    }
                }
        }
    }
}
