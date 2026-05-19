package com.example.showtime.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginUI(
    state : LoginState,
    onAction: (LoginIntent) -> Unit
)
{
    Row{

    }
    Row {
        Column(
            modifier = Modifier.background(color = Color.LightGray)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(text = "Username")
            OutlinedTextField(
                value = state.username,
                onValueChange = {
                    onAction(LoginIntent.ChangeUsername(it))
                },
                supportingText = { state.usernameError?.let { Text(text = it) } },
                isError = (state.usernameError != null)
            )
            Text(text = "Password")
            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    onAction(LoginIntent.ChangePassword(it))
                },
                visualTransformation = PasswordVisualTransformation(),
                supportingText = { state.passwordError?.let { Text(text = it) } },
                isError = (state.passwordError != null)
            )
            Text(text = state.generalError?:"")
            Button(
                onClick = {
                    onAction(LoginIntent.Login)
                },
                enabled = !state.isLogging
            )
            {
                Text(text = "Login")
            }

        }
    }

}