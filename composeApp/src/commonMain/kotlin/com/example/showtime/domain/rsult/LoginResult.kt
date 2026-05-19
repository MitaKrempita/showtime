package com.example.showtime.domain.rsult

sealed interface LoginResult {
    data object Success : LoginResult
    data class Error(val message: String) : LoginResult
}