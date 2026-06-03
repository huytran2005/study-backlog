package com.example.brainnote.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainnote.feature.auth.repository.AuthRepository
import com.example.brainnote.feature.auth.repository.DefaultAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = DefaultAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        var isValid = true

        // 1. Email validation
        val emailError = when {
            currentState.email.isBlank() -> "Email cannot be empty."
            !Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(currentState.email) -> "Please enter a valid email address."
            else -> null
        }
        if (emailError != null) isValid = false

        // 2. Password validation
        val passwordError = when {
            currentState.password.isBlank() -> "Password cannot be empty."
            currentState.password.length < 6 -> "Password must be at least 6 characters."
            else -> null
        }
        if (passwordError != null) isValid = false

        _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }

        if (isValid) {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                val result = authRepository.login(currentState.email, currentState.password)
                _uiState.update { it.copy(isLoading = false) }
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    // Optional: handle failure (e.g. show snackbar, set generic error)
                }
            }
        }
    }

    fun googleLogin(onSuccess: () -> Unit) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = authRepository.googleLogin()
            _uiState.update { it.copy(isLoading = false) }
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}
