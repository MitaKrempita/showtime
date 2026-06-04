package com.example.showtime.presentation.profile

import com.example.showtime.domain.model.User

data class ProfileState(
    val user: User? = null,
    val bestScore: Float = 0f,
    val gamesPlayed: Int = 0,
    val favoriteCount: Int = 0,
    val watchlistCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
