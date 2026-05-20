package com.example.showtime.domain.repository

import com.example.showtime.domain.result.AuthResult

interface AuthRepository {
    suspend fun login(username : String, password: String) : AuthResult
    suspend fun signup(fullName : String,username : String, password: String) : AuthResult
}