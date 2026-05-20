package com.example.showtime.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.showtime.domain.repository.AuthRepository
import com.example.showtime.domain.result.AuthResult
import com.example.showtime.domain.validation.ValidationResult
import com.example.showtime.domain.validation.fullNameVerification
import com.example.showtime.domain.validation.passwordVerification
import com.example.showtime.domain.validation.usernameVerification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignupViewModel(
   val repository : AuthRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(SignupState())
    val state : StateFlow<SignupState> = _uiState


    fun onAction(intent: SignupIntent)
    {
        when(intent)
        {
            is SignupIntent.ChangeFullName -> changeFullName(intent.value)
            is SignupIntent.ChangeUsername -> changeUsername(intent.value)
            is SignupIntent.ChangePassword -> changePassword(intent.value)
            is SignupIntent.Signup -> signup()
        }
    }


    private fun changeFullName(fullName : String)
    {
        val verification = fullNameVerification(fullName)
        _uiState.update {
            currentState -> currentState.copy(fullName = fullName,
                fullNameError = if(verification is ValidationResult.Error)verification.message else null,
                isValidFullName = verification is ValidationResult.Success
                )
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
        viewModelScope.launch {
            val verification = usernameVerification(username)
            _uiState.update { currentState -> currentState.copy(username = username,
                isValidUsername = verification is ValidationResult.Success,
                usernameError = if(verification is ValidationResult.Error)verification.message else null)}
        }
    }
    private fun signup()
    {
        val currentState = _uiState.value
        if(!currentState.isValidPassword || !currentState.isValidUsername || !currentState.isValidFullName || currentState.isLoading) return

        viewModelScope.launch {
            try {
                _uiState.update { currentState -> currentState.copy(isLoading = true) }
                val response = repository.signup(currentState.fullName, currentState.username,currentState.password)
                _uiState.update {
                        currentState -> currentState.copy(generalError = if (response is AuthResult.Error) response.message else null)
                }
            }
            finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


}