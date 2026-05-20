package com.example.showtime.presentation.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.showtime.infrastructure.datastore.getExprValue
import com.example.showtime.presentation.login.LoginIntent

@Composable
fun SignupUI(
    state : SignupState,
    onAction: (SignupIntent) -> Unit
)
{
    Row {
        Column(
            modifier = Modifier.background(color = Color.LightGray)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(text = "Full Name")
            OutlinedTextField(
                value = state.fullName,
                onValueChange = {
                    onAction(SignupIntent.ChangeFullName(it))
                },
                supportingText = { state.fullNameError?.let { Text(text = it) } },
                isError = (state.fullNameError != null)
            )
            Text(text = "Username")
            OutlinedTextField(
                value = state.username,
                onValueChange = {
                    onAction(SignupIntent.ChangeUsername(it))
                },
                supportingText = { state.usernameError?.let { Text(text = it) } },
                isError = (state.usernameError != null)
            )
            Text(text = "Password")
            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    onAction(SignupIntent.ChangePassword(it))
                },
                visualTransformation = PasswordVisualTransformation(),
                supportingText = { state.passwordError?.let { Text(text = it) } },
                isError = (state.passwordError != null)
            )
            Text(text = state.generalError?:"")
            Button(
                onClick = {
                    onAction(SignupIntent.Signup)
                },
                enabled = !state.isLoading
            )
            {
                Text(text = "Sign up")
            }

        }
    }
}
