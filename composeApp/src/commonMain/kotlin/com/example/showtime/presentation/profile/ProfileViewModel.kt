package com.example.showtime.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.repository.AuthRepository
import com.example.showtime.domain.repository.MovieRepository
import com.example.showtime.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val movieRepository: MovieRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()
    private val events = MutableSharedFlow<ProfileIntent>(extraBufferCapacity = 16)

    init {
        viewModelScope.launch {
            events.collect { event ->
                handleEvent(event)
            }
        }
        onEvent(ProfileIntent.LoadData)
    }

    fun onEvent(event: ProfileIntent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    private fun handleEvent(event: ProfileIntent) {
        when (event) {
            ProfileIntent.LoadData -> loadData()
            ProfileIntent.Retry -> loadData()
            ProfileIntent.Logout -> logout()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userResult = authRepository.getCurrentUser()
            if (userResult.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = userResult.exceptionOrNull()?.message
                    )
                }
                return@launch
            }

            val favoriteSync = movieRepository.syncFavorites()
            val watchlistSync = movieRepository.syncWatchlist()
            val bestScore = quizRepository.getLocalBestScore()
            val gamesPlayed = quizRepository.getLocalGamesPlayedCount()
            val favoriteCount = movieRepository.getFavoriteCount()
            val watchlistCount = movieRepository.getWatchlistCount()
            val user = userResult.getOrNull()
            val syncError = favoriteSync.exceptionOrNull()?.message
                ?: watchlistSync.exceptionOrNull()?.message

            _uiState.update {
                it.copy(
                    user = user,
                    bestScore = bestScore,
                    gamesPlayed = gamesPlayed,
                    favoriteCount = favoriteCount,
                    watchlistCount = watchlistCount,
                    isLoading = false,
                    errorMessage = if (user == null && syncError != null) syncError else null
                )
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
