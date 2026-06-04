package com.example.showtime.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.model.QuizQuestion
import com.example.showtime.domain.repository.QuizRepository
import com.example.showtime.domain.useCase.GenerateQuizUseCase
import com.example.showtime.presentation.quiz.main.QuizIntent
import com.example.showtime.presentation.quiz.main.QuizState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.min

class QuizViewModel(
    private val useCase: GenerateQuizUseCase,
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizState())
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()
    private val events = MutableSharedFlow<QuizIntent>(extraBufferCapacity = 16)

    private var questions: List<QuizQuestion> = emptyList()
    private var timerJob: Job? = null
    private var feedbackJob: Job? = null
    private var correctAnswers = 0
    private var incorrectAnswers = 0
    private var isShowingAnswer = false

    init {
        viewModelScope.launch {
            events.collect { event ->
                handleEvent(event)
            }
        }
        initializeQuiz()
    }

    fun onEvent(event: QuizIntent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    private fun handleEvent(event: QuizIntent) {
        when (event) {
            is QuizIntent.SelectAnswer -> processAnswer(event.answer)
            QuizIntent.AbandonQuiz -> abandonQuiz()
        }
    }

    private fun initializeQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            questions = useCase.execute()

            if (questions.size < 10) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isFinished = true,
                        errorMessage = "Browse the catalog first to populate your quiz pool."
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentQuestionIndex = 0,
                    timerSeconds = 60,
                    isFinished = false,
                    finalScore = null,
                    errorMessage = null
                )
            }
            loadQuestion(0)
            startTimer()
        }
    }

    private fun loadQuestion(index: Int) {
        val q = questions[index]
        _uiState.update {
            it.copy(
                currentQuestionIndex = index,
                questionType = q.type,
                movieImageUrl = q.imageUrl,
                movieTitle = q.movieTitle,
                options = q.options,
                correctAnswer = q.correctAnswer,
                selectedAnswer = null
            )
        }
    }

    private fun processAnswer(answer: String) {
        if (_uiState.value.selectedAnswer != null || _uiState.value.isFinished) return

        _uiState.update { it.copy(selectedAnswer = answer) }

        if (answer == _uiState.value.correctAnswer) {
            correctAnswers++
        } else {
            incorrectAnswers++
        }

        feedbackJob = viewModelScope.launch {
            isShowingAnswer = true
            delay(1500)
            isShowingAnswer = false
            val nextIndex = _uiState.value.currentQuestionIndex + 1
            if (nextIndex < 10 && _uiState.value.timerSeconds > 0 && !_uiState.value.isFinished) {
                loadQuestion(nextIndex)
            } else {
                finishQuiz()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSeconds > 0 && !_uiState.value.isFinished) {
                delay(1000)
                if (!_uiState.value.isFinished && !isShowingAnswer) {
                    _uiState.update { it.copy(timerSeconds = it.timerSeconds - 1) }
                    if (_uiState.value.timerSeconds <= 0) {
                        finishQuiz()
                    }
                }
            }
        }
    }

    private fun finishQuiz() {
        if (_uiState.value.isFinished) return
        timerJob?.cancel()
        feedbackJob?.cancel()

        val pvt = _uiState.value.timerSeconds
        val mvt = 60f
        val rawScore = correctAnswers * (9 + (pvt / mvt))
        val finalScore = min(rawScore, 100f)

        val timeUsed = 60 - pvt
        val finalIncorrect = incorrectAnswers + (10 - correctAnswers - incorrectAnswers)

        _uiState.update {
            it.copy(
                isFinished = true,
                finalScore = finalScore,
                correctCount = correctAnswers,
                incorrectCount = finalIncorrect,
                timeUsed = timeUsed,
                isSavingResult = true
            )
        }

        viewModelScope.launch {
            repository.saveQuizResult(finalScore, correctAnswers, finalIncorrect, timeUsed)
            _uiState.update { it.copy(isSavingResult = false) }
        }
    }

    private fun abandonQuiz() {
        timerJob?.cancel()
        feedbackJob?.cancel()
        _uiState.update { it.copy(isFinished = true, isAbandoned = true) }
    }
}
