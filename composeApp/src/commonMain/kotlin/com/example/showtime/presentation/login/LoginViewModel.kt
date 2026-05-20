package com.example.showtime.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.repository.AuthRepository
import com.example.showtime.domain.result.AuthResult
import com.example.showtime.domain.validation.ValidationResult
import com.example.showtime.domain.validation.passwordVerification
import com.example.showtime.domain.validation.usernameVerification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val state : StateFlow<LoginState> = _uiState


    fun onAction(intent: LoginIntent)
    {
        when(intent)
        {
            is LoginIntent.ChangeUsername -> changeUsername(intent.username)
            is LoginIntent.ChangePassword ->  changePassword(intent.password)
            is LoginIntent.Login -> login()
        }
    }
    private fun login()
    {
        val currentState = _uiState.value
        if(!currentState.isValidPassword || !currentState.isValidUsername || currentState.isLogging) return

        viewModelScope.launch {try {
            _uiState.update { currentState -> currentState.copy(isLogging = true) }
            val response = repository.login(currentState.username, currentState.password)
            _uiState.update {
                currentState -> currentState.copy(generalError = if (response is AuthResult.Error) response.message else null)
            }
        }finally {
            _uiState.update { it.copy(isLogging = false) }
        }
        }
    }
    private fun changePassword(password: String)
    {
        val verification = passwordVerification(password)
        _uiState.update { currentState -> currentState.copy(password = password,
            isValidPassword = verification is ValidationResult.Success,
            passwordError = if(verification is ValidationResult.Error)verification.message else null) }
    }

     private fun changeUsername(username: String)
    {
        viewModelScope.launch { //za slucaj da bude trebala provera u databazi, al mi nelogicno malo
            val verification = usernameVerification(username)
            _uiState.update { currentState -> currentState.copy(username = username,
                isValidUsername = verification is ValidationResult.Success,
                usernameError = if(verification is ValidationResult.Error)verification.message else null)}
        }
    }

}