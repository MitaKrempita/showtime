package com.example.showtime.domain.repository

import com.example.showtime.domain.rsult.LoginResult

interface AuthRepository {
    suspend fun login(username : String, password: String) : LoginResult
}