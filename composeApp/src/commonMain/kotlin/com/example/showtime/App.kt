package com.example.showtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.showtime.presentation.login.LoginUI
import com.example.showtime.presentation.login.LoginViewModel
import com.example.showtime.presentation.signup.SignupUI
import com.example.showtime.presentation.signup.SignupViewModel
import com.example.showtime.presentation.startup.StartUpUI
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.STARTUP
    ){
        composable(Routes.STARTUP) {
            StartUpUI(
                onLogIn = { navController.navigate(Routes.LOGIN) },
                onSignUp = { navController.navigate(Routes.SIGNUP) }
            )
        }
        composable(Routes.LOGIN) {
            val viewModel: LoginViewModel = koinViewModel()
            val state by viewModel.state.collectAsState()
            LoginUI(
                state = state,
                onAction =
                    { action ->
                        viewModel.onAction(action)
                    }
            )
        }
        composable(Routes.SIGNUP) {
            val viewModel : SignupViewModel =koinViewModel()
            val state by viewModel.state.collectAsState()
            SignupUI(
                state = state,
                onAction =
                    { action ->
                        viewModel.onAction(action)
                    }
            )
        }
    }


   /*
     */
}