package com.example.showtime.domain.validation

fun passwordVerification(password : String) : ValidationResult
{
    return if(password.length < 8) {
        ValidationResult.Error("Password must be at least 8 characters long")
    }
    else ValidationResult.Success
}
