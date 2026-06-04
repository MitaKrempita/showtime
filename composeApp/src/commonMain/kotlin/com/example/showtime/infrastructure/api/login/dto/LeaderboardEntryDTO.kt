package com.example.showtime.infrastructure.api.login.dto

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntryDTO(
    val rank : Int,
    val user_id : Int,
    val username : String,
    val full_name : String,
    val score : Float,
    val played_at : Long,
    val total_plays : Int
)

