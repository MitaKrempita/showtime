package com.example.showtime.domain

import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.data.LoginRequest
import com.example.showtime.infrastructure.data.SignupRequest

interface LoginAPI
{
    suspend fun loginPost(requestLogin : LoginRequest): ApiResult
    suspend fun signupPost(requestSignup : SignupRequest) : ApiResult
}