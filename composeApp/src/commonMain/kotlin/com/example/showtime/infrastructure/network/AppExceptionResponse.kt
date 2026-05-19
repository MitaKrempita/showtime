package com.example.showtime.infrastructure.network

data class AppExceptionResponse(
    val error: String,
    val httpCode : Int,
    val message: String
)