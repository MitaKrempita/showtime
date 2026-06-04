package com.example.showtime.presentation.quiz.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizStartViewModel(
    private val repository: QuizRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(QuizStartState())
    val uiState: StateFlow<QuizStartState> = _uiState.asStateFlow()
    private val events = MutableSharedFlow<QuizStartIntent>(extraBufferCapacity = 16)

    init {
        viewModelScope.launch {
            events.collect { event ->
                handleEvent(event)
            }
        }
        onEvent(QuizStartIntent.LoadData)
    }

    fun onEvent(event: QuizStartIntent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    private fun handleEvent(event: QuizStartIntent) {
        when (event) {
            QuizStartIntent.LoadData -> loadData()
            QuizStartIntent.Retry -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val movieCount = repository.getLocalMoviesCount()
            var bestScore = repository.getLocalBestScore()
            var gamesPlayed = repository.getLocalGamesPlayedCount()

            _uiState.update {
                it.copy(
                    personalBest = bestScore,
                    gamesPlayed = gamesPlayed,
                    canStartQuiz = movieCount >= 10
                )
            }

            try {
                repository.syncQuizResults(page = 1, pageSize = 100)
                bestScore = repository.getLocalBestScore()
                gamesPlayed = repository.getLocalGamesPlayedCount()
            } catch (e: Exception) {
            }

            repository.getLeaderboard(page = 1, pageSize = 20)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            canStartQuiz = movieCount >= 10,
                            personalBest = bestScore,
                            gamesPlayed = gamesPlayed,
                            leaderBoardData = list,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            canStartQuiz = movieCount >= 10,
                            personalBest = bestScore,
                            gamesPlayed = gamesPlayed,
                            errorMessage = if (movieCount >= 10) null else error.message
                        )
                    }
                }
        }
    }
}
