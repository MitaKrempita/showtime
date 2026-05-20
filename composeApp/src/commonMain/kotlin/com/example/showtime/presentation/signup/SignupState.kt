package com.example.showtime.presentation.signup

data class SignupState (
    val fullName : String = "",
    val username : String = "",
    val password : String = "",
    val isValidFullName : Boolean = false,
    val isValidPassword : Boolean = false,
    val isValidUsername : Boolean = false,
    val passwordError : String? = null,
    val usernameError : String? = null,
    val fullNameError : String? = null,
    val generalError : String? = null,
    val isLoading : Boolean = false
)