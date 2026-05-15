package com.example.showtime.domain.validation

sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Error(
        val message : String
    ) : ValidationResult
    data object Loading: ValidationResult
}