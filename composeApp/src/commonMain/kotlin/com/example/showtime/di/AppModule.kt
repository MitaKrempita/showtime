    package com.example.showtime.di

    import androidx.datastore.core.DataStore
    import androidx.datastore.preferences.core.Preferences
    import com.example.showtime.domain.LoginAPI
    import com.example.showtime.domain.repository.AuthRepository
    import com.example.showtime.infrastructure.AuthRepositoryImpl
    import com.example.showtime.infrastructure.api.login.KtorLoginApi
    import com.example.showtime.infrastructure.datastore.DATA_STORE_FILE_NAME
    import com.example.showtime.infrastructure.datastore.createPreferencesDataStore
    import com.example.showtime.presentation.login.LoginState
    import com.example.showtime.presentation.login.LoginViewModel
    import com.example.showtime.presentation.signup.SignupState
    import com.example.showtime.presentation.signup.SignupViewModel
    import io.ktor.client.HttpClient
    import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
    import io.ktor.serialization.kotlinx.json.json
    import kotlinx.serialization.json.Json
    import org.koin.core.module.dsl.singleOf
    import org.koin.core.module.dsl.viewModelOf
    import org.koin.dsl.bind
    import org.koin.dsl.module


    val dataModule = module {
        single(createdAtStart = true) {buildClient()}
        single{LoginState()}
        single{ SignupState() }


        singleOf(::AuthRepositoryImpl) bind AuthRepository::class
        singleOf(::KtorLoginApi) bind LoginAPI::class
        viewModelOf(::LoginViewModel)
        viewModelOf(::SignupViewModel)

    }
    private fun buildClient() : HttpClient
    {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        return HttpClient {
            install(ContentNegotiation) {
                json(json
                //    , contentType = ContentType.Any
                )
            }
        }
    }