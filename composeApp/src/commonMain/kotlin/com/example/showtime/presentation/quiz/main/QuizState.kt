package com.example.showtime.presentation.quiz.main

data class QuizState(
    val currentQuestionIndex: Int = 0,
    val questionType: QuestionType = QuestionType.MOVIE,
    val movieImageUrl: String? = null,
    val movieTitle: String = "",
    val options: List<String> = emptyList(),
    val selectedAnswer: String? = null,
    val correctAnswer: String = "",
    val timerSeconds: Int = 60,
    val isLoading: Boolean = false,
    val isFinished: Boolean = false,
    val finalScore: Float? = null,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val timeUsed: Int = 0,
    val errorMessage: String? = null,
    val isAbandoned: Boolean = false,
    val isSavingResult: Boolean = false
) {
    val isAnswered: Boolean get() = selectedAnswer != null
}
