package com.example.showtime.infrastructure
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.domain.LoginAPI
import com.example.showtime.domain.repository.AuthRepository
import com.example.showtime.domain.result.AuthResult
import com.example.showtime.infrastructure.data.LoginRequest
import com.example.showtime.infrastructure.data.SignupRequest
import com.example.showtime.infrastructure.datastore.setExprValue
import com.example.showtime.infrastructure.datastore.setToken
import com.example.showtime.infrastructure.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AuthRepositoryImpl(
    private val api : LoginAPI,
    private val dataStore: DataStore<Preferences>
) : AuthRepository {


    override suspend fun login(username:String,password : String) : AuthResult  = withContext(Dispatchers.IO) {
        System.out.println("LOGIN:$username $password")
        val token = api.loginPost(LoginRequest(username,password))
        when(token) {
            is ApiResult.Success -> {
                setToken(dataStore, token.data.access_token)
                setExprValue(dataStore, token.data.expires_in)
                //TODO: mapper za user-a tkd feedback
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
    ) : AuthResult  = withContext(Dispatchers.IO){
        System.out.println("SIGNUP: $fullName $username $password")
        val token = api.signupPost(SignupRequest(fullName,username,password))
        when(token)
        {
            is ApiResult.Success ->{
                setToken(dataStore, token.data.access_token)
                setExprValue(dataStore, token.data.expires_in)
                //TODO: mapper za user-a tkd feedback
                AuthResult.Success}
            is ApiResult.Error -> {
                AuthResult.Error(token.message.message)
            }
        }

    }
}