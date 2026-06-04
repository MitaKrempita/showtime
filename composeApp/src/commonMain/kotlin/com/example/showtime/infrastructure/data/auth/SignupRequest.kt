package com.example.showtime.infrastructure.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val full_name: String,
    val username : String,
    val password : String
)
