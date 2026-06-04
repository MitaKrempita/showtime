package com.example.showtime.domain.repository

import com.example.showtime.domain.model.User
import com.example.showtime.domain.result.AuthResult

interface AuthRepository {
    suspend fun login(username : String, password: String) : AuthResult
    suspend fun signup(fullName : String,username : String, password: String) : AuthResult
    suspend fun getCurrentUser() : Result<User>
    suspend fun logout()
}
