package com.example.showtime.domain.repository

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface BaseRepository {
    fun checkUserLoginStatusFlow(): Flow<Boolean>
}