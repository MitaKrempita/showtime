package com.example.showtime.domain.validation

fun passwordCensorship(password : String) : String
{
    return "*".repeat(password.length)
}