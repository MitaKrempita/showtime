package com.example.showtime.infrastructure.implementation.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.domain.repository.BaseRepository
import com.example.showtime.infrastructure.datastore.clearAuth
import com.example.showtime.infrastructure.datastore.TOKEN_KEY
import com.example.showtime.infrastructure.datastore.EXPIRATION_VALUE
import com.example.showtime.infrastructure.room.dao.MovieDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class BaseRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val movieDao: MovieDao
) : BaseRepository {
    override fun checkUserLoginStatusFlow(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            val token = preferences[TOKEN_KEY]
            val expr = preferences[EXPIRATION_VALUE]
            if (token == null || expr == null) {
                false
            } else if (Instant.now().toEpochMilli() < expr) {
                true
            } else {
                clearAuth(dataStore)
                movieDao.clearFavorites()
                movieDao.clearWatchlist()
                false
            }
        }
    }
}
