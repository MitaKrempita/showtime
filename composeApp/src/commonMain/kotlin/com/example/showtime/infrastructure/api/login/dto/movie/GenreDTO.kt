package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class GenreDTO (
    val id : Int,
    val name :String
){
}