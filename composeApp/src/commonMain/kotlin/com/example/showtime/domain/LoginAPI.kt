package com.example.showtime.domain

import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.data.LoginRequest

interface LoginAPI
{
    suspend fun loginPost(requestLogin : LoginRequest): ApiResult
}