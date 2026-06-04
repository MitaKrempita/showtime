package com.example.showtime.infrastructure.api.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.domain.api.LoginAPI
import com.example.showtime.infrastructure.api.login.dto.AuthResponseDTO
import com.example.showtime.infrastructure.api.login.dto.UserDTO
import com.example.showtime.infrastructure.data.auth.LoginRequest
import com.example.showtime.infrastructure.data.auth.SignupRequest
import com.example.showtime.infrastructure.datastore.getValidToken
import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.network.AppExceptionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val BASE_URL = "https://rma.finlab.rs"
class KtorLoginApi(
    private val client : HttpClient,
    private val dataStore: DataStore<Preferences>
) : LoginAPI
{
    private companion object
    {
        const val logInURL = "${BASE_URL}/auth/login"
        const val signUpURL = "${BASE_URL}/auth/signup"
        const val meURL = "${BASE_URL}/me"
    }
    override suspend fun loginPost(requestLogin : LoginRequest): ApiResult<AuthResponseDTO> {
        return try {
            val response =  client.post(logInURL)
                { contentType(ContentType.Application.Json)
                    setBody(requestLogin)}
            if(!response.status.isSuccess())
            {
                return createExceptionResult(response)
            }
            ApiResult.Success(response.body())
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("NetworkError", 0, e.message ?: "Unknown connection error."))
        }
    }

    override suspend fun signupPost(requestSignup: SignupRequest): ApiResult<AuthResponseDTO> {
        return try {
            val response = client.post(signUpURL)
            {
                contentType(ContentType.Application.Json)
                setBody(requestSignup)
            }
            if(!response.status.isSuccess())
            {
                return createExceptionResult(response)
            }
            ApiResult.Success(response.body())
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("NetworkError", 0, e.message ?: "Unknown connection error."))
        }
    }
    override suspend fun getMe(): ApiResult<UserDTO> {
        return try {
            val response = client.get(meURL) {
                getValidToken(dataStore)?.let { header("Authorization", "Bearer $it") }
            }
            if(!response.status.isSuccess())
            {
                return createExceptionResult(response)
            }
            ApiResult.Success(response.body())
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("NetworkError", 0, e.message ?: "Unknown connection error."))
        }
    }
    private suspend fun createExceptionResult(response : HttpResponse) : ApiResult<Nothing>
    {
        return try {
            val jsonElements = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val errorJson : String= jsonElements["error"]?.jsonPrimitive?.content?:""
            val httpCodeJson : Int = jsonElements["httpCode"]?.jsonPrimitive?.int ?: response.status.value
            val messageJson : String= jsonElements["message"]?.jsonPrimitive?.content?:""
            ApiResult.Error(AppExceptionResponse(errorJson,httpCodeJson,messageJson))
        } catch (e: Exception) {
            ApiResult.Error(AppExceptionResponse("ParsingError", response.status.value, "Failed to parse error response."))
        }
    }
}
