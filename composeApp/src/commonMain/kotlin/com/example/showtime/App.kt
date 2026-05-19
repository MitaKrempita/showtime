package com.example.showtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.showtime.presentation.login.LoginUI
import com.example.showtime.presentation.login.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App( prefs: DataStore<Preferences>) {

   // val navController = rememberNavController() dependency later
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