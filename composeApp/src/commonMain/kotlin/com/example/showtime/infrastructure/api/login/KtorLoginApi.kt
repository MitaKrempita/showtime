package com.example.showtime.infrastructure.api.login

import com.example.showtime.domain.LoginAPI
import com.example.showtime.infrastructure.data.LoginRequest
import com.example.showtime.infrastructure.network.ApiResult
import com.example.showtime.infrastructure.network.AppExceptionResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


private object url {
    const val baseUrl = "https://rma.finlab.rs"}
class KtorLoginApi(private val client : HttpClient) : LoginAPI
{
    private companion object
    {
        const val call = "${url.baseUrl}/auth/login"
    }
    override suspend fun loginPost(requestLogin : LoginRequest): ApiResult {

         val response =  client.post(call)
            { contentType(ContentType.Application.Json)
                setBody(requestLogin)}
            if(!response.status.isSuccess())
            {
                val jsonElements = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                val errorJson : String= jsonElements["error"]?.jsonPrimitive?.content?:""
                val httpCodeJson : Int = jsonElements["httpCode"]?.jsonPrimitive?.int ?: 0
                val messageJson : String= jsonElements["message"]?.jsonPrimitive?.content?:""
                return ApiResult.Error(AppExceptionResponse(errorJson,httpCodeJson,messageJson))
            }
        return ApiResult.Success(response.body())
    }

}