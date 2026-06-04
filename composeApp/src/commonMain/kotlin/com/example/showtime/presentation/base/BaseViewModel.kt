package com.example.showtime.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.repository.BaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BaseViewModel(
    repository: BaseRepository
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean?> = repository.checkUserLoginStatusFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}