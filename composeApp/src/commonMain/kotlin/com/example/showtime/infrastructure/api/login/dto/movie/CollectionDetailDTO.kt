package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class CollectionDetailDTO(
    val collection : CollectionDTO,
    val movies : List<MovieListItemDTO>
) {
}