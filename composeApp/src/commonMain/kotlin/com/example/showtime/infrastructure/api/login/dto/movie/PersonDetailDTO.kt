package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class PersonDetailDTO(
    val person : PersonDTO,
    val movies : List<MovieListItemDTO>
) {
}