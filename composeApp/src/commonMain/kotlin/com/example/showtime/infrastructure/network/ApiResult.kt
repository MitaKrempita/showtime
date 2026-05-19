package com.example.showtime.infrastructure.network

import com.example.showtime.infrastructure.api.login.dto.AuthResponseDTO

sealed class ApiResult {
    data class Success(val data: AuthResponseDTO) : ApiResult()
    data class Error(val message: AppExceptionResponse) : ApiResult()
}

