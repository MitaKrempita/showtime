package com.example.showtime.presentation.profile

sealed interface ProfileIntent {
    data object LoadData : ProfileIntent
    data object Retry : ProfileIntent
    data object Logout : ProfileIntent
}
