package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponseDTO<T>(
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<T>
) {
}