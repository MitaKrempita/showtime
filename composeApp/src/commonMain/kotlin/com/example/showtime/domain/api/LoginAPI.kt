package com.example.showtime.domain.api

import com.example.showtime.infrastructure.api.login.dto.AuthResponseDTO
import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.data.LoginRequest
import com.example.showtime.infrastructure.data.SignupRequest

interface LoginAPI
{
    suspend fun loginPost(requestLogin : LoginRequest): ApiResult<AuthResponseDTO>
    suspend fun signupPost(requestSignup : SignupRequest) : ApiResult<AuthResponseDTO>
}