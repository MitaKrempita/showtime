package com.example.showtime.infrastructure.api.login.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDTO(
    val access_token : String,
    val expires_in : Long,
    val user : UserDTO
)
