package com.example.showtime.domain.validation

fun passwordVerification(password : String) : ValidationResult
{
    if(password.length<=8) {
        return ValidationResult.Error("Password must be at least 8 characters long");
    }
    else return ValidationResult.Success
}