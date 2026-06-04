package com.example.showtime.infrastructure.api.login.dto.movie

import kotlinx.serialization.Serializable

@Serializable
data class ProductionCompanyDTO(
    val id : Int,
    val name : String,
    val logoPath : String?,
    val originCountry : String?
)