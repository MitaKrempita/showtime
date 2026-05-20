package com.example.showtime.infrastructure.data

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val full_name: String,
    val username : String,
    val password : String
)
