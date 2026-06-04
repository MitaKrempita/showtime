package com.example.showtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.showtime.presentation.LocalNavController
import com.example.showtime.presentation.base.BaseViewModel
import com.example.showtime.presentation.detail.MovieDetailsScreen
import com.example.showtime.presentation.detail.MovieDetailsViewModel
import com.example.showtime.presentation.filter.FilterScreen
import com.example.showtime.presentation.filter.FilterScreenViewModel
import com.example.showtime.presentation.lists.MovieCollectionScreen
import com.example.showtime.presentation.lists.MovieCollectionViewModel
import com.example.showtime.presentation.login.LoginUI
import com.example.showtime.presentation.login.LoginViewModel
import com.example.showtime.presentation.mainScreen.MainScreen
import com.example.showtime.presentation.mainScreen.MainScreenIntent
import com.example.showtime.presentation.mainScreen.MainScreenViewModel
import com.example.showtime.presentation.profile.ProfileScreen
import com.example.showtime.presentation.profile.ProfileViewModel
import com.example.showtime.presentation.quiz.QuizViewModel
import com.example.showtime.presentation.quiz.main.QuizScreen
import com.example.showtime.presentation.quiz.start.QuizStartScreen
import com.example.showtime.presentation.quiz.start.QuizStartViewModel
import com.example.showtime.presentation.signup.SignupUI
import com.example.showtime.presentation.signup.SignupViewModel
import com.example.showtime.presentation.startup.StartUpUI
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    val navController = rememberNavController()
    val baseViewModel: BaseViewModel = koinViewModel()
    val isLoggedIn by baseViewModel.isLoggedIn.collectAsStateWithLifecycle()

    val mainViewModel: MainScreenViewModel = koinViewModel()
    val mainState by mainViewModel.uiState.collectAsStateWithLifecycle()



    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            navController.navigate(StartupRoute) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = InitRoute
        ) {
            composable<InitRoute> {
                LaunchedEffect(isLoggedIn) {
                    when (isLoggedIn) {
                        null -> {}
                        true -> {
                            navController.navigate(MovieRoute) {
                                popUpTo(InitRoute) { inclusive = true }
                            }
                        }

                        false -> {
                            navController.navigate(StartupRoute) {
                                popUpTo(InitRoute) { inclusive = true }
                            }
                        }
                    }
                }
            }

            composable<MovieRoute> {
                MainScreen(
                    state = mainState,
                    onAction = { action -> mainViewModel.onEvent(action) },
                    onOpenFilters = { navController.navigate(FilterRoute) },
                    onOpenMovie = { movieId ->
                        navController.navigate(DetailRoute(movieId))
                    }
                )
            }
            composable<QuizStartRoute> {
                val viewModel: QuizStartViewModel = koinViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                QuizStartScreen(
                    state = state,
                    onAction = { action -> viewModel.onEvent(action) },
                    onStartQuiz = {
                         navController.navigate(QuizRoute)
                    }
                )
            }
            composable<QuizRoute> {
                val viewModel: QuizViewModel = koinViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                QuizScreen(
                    state = state,
                    onAction = { action -> viewModel.onEvent(action) }
                )
            }
            composable<DetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<DetailRoute>()
                val detailsViewModel: MovieDetailsViewModel = koinViewModel {
                    parametersOf(route.movieId)
                }
                val detailsState by detailsViewModel.uiState.collectAsStateWithLifecycle()

                MovieDetailsScreen(
                    uiState = detailsState,
                    onBack = { navController.popBackStack() },
                    onEvent = { action -> detailsViewModel.onEvent(action) }
                )
            }

            composable<FilterRoute> {
                val filterViewModel: FilterScreenViewModel = koinViewModel {
                    parametersOf(mainState.filters)
                }
                val filterState by filterViewModel.uiState.collectAsStateWithLifecycle()

                FilterScreen(
                    state = filterState,
                    onAction = { action -> filterViewModel.onEvent(action) },
                    onBack = { navController.popBackStack() },
                    onApply = { filters ->
                        mainViewModel.onEvent(MainScreenIntent.ApplyFilters(filters))
                        navController.popBackStack()
                    }
                )
            }

            composable<WatchlistRoute> {
                val viewModel: MovieCollectionViewModel = koinViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                MovieCollectionScreen(
                    state = state,
                    onAction = { action -> viewModel.onEvent(action) },
                    onOpenMovie = { movieId -> navController.navigate(DetailRoute(movieId)) }
                )
            }

            composable<ProfileRoute> {
                val viewModel: ProfileViewModel = koinViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                ProfileScreen(
                    state = state,
                    onAction = { action -> viewModel.onEvent(action) }
                )
            }

            composable<StartupRoute> {
                StartUpUI(
                    onLogIn = { navController.navigate(LoginRoute) },
                    onSignUp = { navController.navigate(SignupRoute) }
                )
            }

            composable<LoginRoute> {
                val viewModel: LoginViewModel = koinViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn == true) {
                        navController.navigate(MovieRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                }

                LoginUI(
                    state = state,
                    onAction = { action -> viewModel.onAction(action) }
                )
            }

            composable<SignupRoute> {
                val viewModel: SignupViewModel = koinViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn == true) {
                        navController.navigate(MovieRoute) {
                            popUpTo(SignupRoute) { inclusive = true }
                        }
                    }
                }

                SignupUI(
                    state = state,
                    onAction = { action -> viewModel.onAction(action) }
                )
            }
        }
    }
}
