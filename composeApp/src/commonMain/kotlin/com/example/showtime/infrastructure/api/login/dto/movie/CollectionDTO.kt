package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class CollectionDTO(
    val id : Int,
    val name : String,
    val posterPath : String?,
    val backdropPath : String?
) {
}