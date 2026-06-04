package com.example.showtime.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.domain.api.LoginAPI
import com.example.showtime.domain.api.MovieApi
import com.example.showtime.domain.api.QuizApi
import com.example.showtime.domain.repository.AuthRepository
import com.example.showtime.domain.repository.BaseRepository
import com.example.showtime.domain.repository.MovieRepository
import com.example.showtime.domain.repository.QuizRepository
import com.example.showtime.domain.useCase.GenerateQuizUseCase
import com.example.showtime.infrastructure.implementation.repository.AuthRepositoryImpl
import com.example.showtime.infrastructure.api.login.KtorLoginApi
import com.example.showtime.infrastructure.api.login.KtorMovieApi
import com.example.showtime.infrastructure.api.login.KtorQuizApi
import com.example.showtime.infrastructure.implementation.repository.BaseRepositoryImpl
import com.example.showtime.infrastructure.implementation.repository.MovieRepositoryImplementation
import com.example.showtime.infrastructure.room.AppDatabase
import com.example.showtime.infrastructure.room.dao.MovieDao
import com.example.showtime.infrastructure.datastore.clearAuth
import com.example.showtime.infrastructure.implementation.repository.QuizRepositoryImpl
import com.example.showtime.presentation.base.BaseViewModel
import com.example.showtime.presentation.detail.MovieDetailsViewModel
import com.example.showtime.presentation.filter.FilterScreenViewModel
import com.example.showtime.presentation.lists.MovieCollectionViewModel
import com.example.showtime.presentation.login.LoginState
import com.example.showtime.presentation.login.LoginViewModel
import com.example.showtime.presentation.mainScreen.MainScreenViewModel
import com.example.showtime.presentation.profile.ProfileViewModel
import com.example.showtime.presentation.quiz.QuizViewModel
import com.example.showtime.presentation.quiz.start.QuizStartViewModel
import com.example.showtime.presentation.signup.SignupState
import com.example.showtime.presentation.signup.SignupViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single(createdAtStart = true) { buildClient(get(), get()) }
    single { LoginState() }
    single { SignupState() }

    single { get<AppDatabase>().movieDao() }
    single { get<AppDatabase>().quizDao() }

    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::KtorLoginApi) bind LoginAPI::class
    singleOf(::BaseRepositoryImpl) bind BaseRepository::class
    singleOf(::KtorMovieApi) bind MovieApi::class
    singleOf(::MovieRepositoryImplementation) bind MovieRepository::class
    singleOf(::KtorQuizApi) bind QuizApi::class
    singleOf(::QuizRepositoryImpl) bind QuizRepository::class

    viewModelOf(::BaseViewModel)
    viewModelOf(::MainScreenViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignupViewModel)
    viewModelOf(::FilterScreenViewModel)
    viewModelOf(::QuizStartViewModel)
    viewModelOf(::MovieCollectionViewModel)
    viewModelOf(::ProfileViewModel)
    singleOf(::GenerateQuizUseCase)
    viewModelOf(::QuizViewModel)

    viewModel { params -> MovieDetailsViewModel(movieId = params.get(), repository = get()) }
}

private fun buildClient(dataStore: DataStore<Preferences>, movieDao: MovieDao): HttpClient {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    return HttpClient {
        install(ContentNegotiation) {
            json(json
            //, contentType = ContentType.Any
            )
        }
        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    clearAuth(dataStore)
                    movieDao.clearFavorites()
                    movieDao.clearWatchlist()
                }
            }
        }
    }
}
