package com.example.showtime.infrastructure.implementation.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.domain.api.LoginAPI
import com.example.showtime.domain.model.User
import com.example.showtime.domain.repository.AuthRepository
import com.example.showtime.domain.result.AuthResult
import com.example.showtime.infrastructure.api.login.mapper.toDomain
import com.example.showtime.infrastructure.datastore.clearAuth
import com.example.showtime.infrastructure.data.auth.LoginRequest
import com.example.showtime.infrastructure.data.auth.SignupRequest
import com.example.showtime.infrastructure.datastore.getCachedUser
import com.example.showtime.infrastructure.datastore.setCachedUser
import com.example.showtime.infrastructure.datastore.setExprValue
import com.example.showtime.infrastructure.datastore.setToken
import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.room.dao.MovieDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val api : LoginAPI,
    private val dataStore: DataStore<Preferences>,
    private val movieDao: MovieDao
) : AuthRepository {


    override suspend fun login(username:String,password : String) : AuthResult =
        withContext(Dispatchers.IO) {
            val token = api.loginPost(LoginRequest(username, password))
            when (token) {
                is ApiResult.Success -> {
                    setToken(dataStore, token.data.access_token)
                    setExprValue(dataStore, token.data.expires_in)
                    setCachedUser(dataStore, token.data.user.toDomain())
                    AuthResult.Success
                }

                is ApiResult.Error -> {
                    AuthResult.Error(token.message.message)
                }
            }
        }

    override suspend fun signup (
        fullName: String,
        username: String,
        password: String
    ) : AuthResult = withContext(Dispatchers.IO) {
        val token = api.signupPost(SignupRequest(fullName, username, password))
        when (token) {
            is ApiResult.Success -> {
                setToken(dataStore, token.data.access_token)
                setExprValue(dataStore, token.data.expires_in)
                setCachedUser(dataStore, token.data.user.toDomain())
                AuthResult.Success
            }

            is ApiResult.Error -> {
                AuthResult.Error(token.message.message)
            }
        }

    }

    override suspend fun getCurrentUser(): Result<User> = withContext(Dispatchers.IO) {
        when (val result = api.getMe()) {
            is ApiResult.Success -> {
                val user = result.data.toDomain()
                setCachedUser(dataStore, user)
                Result.success(user)
            }
            is ApiResult.Error -> {
                if (result.message.httpCode == 401) {
                    logout()
                }
                val cachedUser = getCachedUser(dataStore)
                if (result.message.httpCode != 401 && cachedUser != null) {
                    return@withContext Result.success(cachedUser)
                }
                Result.failure(Exception(result.message.message))
            }
        }
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        clearAuth(dataStore)
        movieDao.clearFavorites()
        movieDao.clearWatchlist()
    }
}
