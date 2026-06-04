package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class ConfigEntryDTO(
    val key: String,
    val value: String
)