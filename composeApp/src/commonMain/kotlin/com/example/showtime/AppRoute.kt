package com.example.showtime

import kotlinx.serialization.Serializable

@Serializable
object InitRoute

@Serializable
object StartupRoute

@Serializable
object LoginRoute

@Serializable
object SignupRoute

@Serializable
object MovieRoute

@Serializable
object FilterRoute

@Serializable
data class DetailRoute(val movieId: String)
@Serializable
object QuizStartRoute
@Serializable
object WatchlistRoute


@Serializable
object ProfileRoute
@Serializable
object QuizRoute