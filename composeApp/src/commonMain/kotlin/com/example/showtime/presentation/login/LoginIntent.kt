package com.example.showtime.presentation.login

sealed interface LoginIntent {
    data class ChangeUsername(val username: String) : LoginIntent
    data class ChangePassword(val password : String) : LoginIntent
    data object Login : LoginIntent
}