package com.example.showtime.infrastructure
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.domain.LoginAPI
import com.example.showtime.domain.repository.AuthRepository
import com.example.showtime.domain.rsult.LoginResult
import com.example.showtime.infrastructure.data.LoginRequest
import com.example.showtime.infrastructure.datastore.setExprValue
import com.example.showtime.infrastructure.datastore.setToken
import com.example.showtime.infrastructure.network.ApiResult

class AuthRepositoryImpl(
    private val api : LoginAPI,
    private val dataStore: DataStore<Preferences>
) : AuthRepository {


    override suspend fun login(username:String,password : String) : LoginResult {
        val token = api.loginPost(LoginRequest(username,password))
        return when(token) {
            is ApiResult.Success -> {
                setToken(dataStore, token.data.access_token)
                setExprValue(dataStore, token.data.expires_in)
                //TODO: mapper za user-a tkd feedback
                LoginResult.Success
            }
            is ApiResult.Error -> {
                LoginResult.Error(token.message.message)
            }
        }
    }
}