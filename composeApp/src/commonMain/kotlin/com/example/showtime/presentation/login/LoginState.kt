package com.example.showtime.presentation.login

data class LoginState(
    val username: String = "",
    val password : String = "",
    val isLogging: Boolean = false,
    val isValidPassword : Boolean = false,
    val isValidUsername : Boolean = false,
    val passwordError : String? = null,
    val usernameError : String? = null,
    val generalError : String? = null,
    val isLoading : Boolean = false //nvrm da je potrebno
)