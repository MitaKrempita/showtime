package com.example.showtime.infrastructure.api.login.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id : Long,
    val username : String,
    val full_name : String
)
