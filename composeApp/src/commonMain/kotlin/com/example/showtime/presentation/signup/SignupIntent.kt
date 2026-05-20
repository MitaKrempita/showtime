package com.example.showtime.presentation.signup

sealed interface SignupIntent {
    data class ChangeFullName(val value : String) : SignupIntent
    data class ChangeUsername(val value : String) : SignupIntent
    data class ChangePassword(val value : String) : SignupIntent
    data object Signup : SignupIntent
}