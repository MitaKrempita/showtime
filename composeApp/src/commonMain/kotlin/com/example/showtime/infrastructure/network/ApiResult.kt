package com.example.showtime.infrastructure.network

sealed interface ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>
    data class Error(val message: AppExceptionResponse) : ApiResult<Nothing>
}

