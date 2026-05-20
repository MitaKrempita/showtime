package com.example.showtime.infrastructure.api.login

import com.example.showtime.domain.LoginAPI
import com.example.showtime.infrastructure.data.LoginRequest
import com.example.showtime.infrastructure.data.SignupRequest
import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.network.AppExceptionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
class KtorLoginApi(private val client : HttpClient) : LoginAPI
{
    private companion object
    {
        const val logInURL = "${BASE_URL}/auth/login"
        const val signUpURL = "${BASE_URL}/auth/signup"
    }
    override suspend fun loginPost(requestLogin : LoginRequest): ApiResult {
        System.out.println("LOG IN POST :"+requestLogin)
         val response =  client.post(logInURL)
            { contentType(ContentType.Application.Json)
                setBody(requestLogin)}
            if(!response.status.isSuccess())
            {
                return createExceptionResult(response)
            }
        return ApiResult.Success(response.body())
    }

    override suspend fun signupPost(requestSignup: SignupRequest): ApiResult {
        System.out.println("SIGN UP POST :"+requestSignup)
        val response = client.post(signUpURL)
        {
            contentType(ContentType.Application.Json)
            setBody(requestSignup)
        }
        if(!response.status.isSuccess())
        {
            return createExceptionResult(response)
        }
        return ApiResult.Success(response.body())
    }
    private suspend fun createExceptionResult(response : HttpResponse) : ApiResult
    {
        val jsonElements = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        val errorJson : String= jsonElements["error"]?.jsonPrimitive?.content?:""
        val httpCodeJson : Int = jsonElements["httpCode"]?.jsonPrimitive?.int ?: 0
        val messageJson : String= jsonElements["message"]?.jsonPrimitive?.content?:""
        return ApiResult.Error(AppExceptionResponse(errorJson,httpCodeJson,messageJson))
    }
}
