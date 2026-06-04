package com.example.brainnote.feature.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brainnote.feature.auth.repository.AuthRepository
import com.example.brainnote.feature.auth.repository.DefaultAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = DefaultAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { currentState ->
            val error = when {
                email.isBlank() -> "Email cannot be empty"
                !Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(email) -> "Please enter a valid email address."
                else -> null
            }
            currentState.copy(
                email = email,
                emailError = error
            )
        }
    }

    fun submitEmail(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.requestPasswordReset(state.email)
            _uiState.update { it.copy(isLoading = false) }
            if (result.isSuccess) {
                _uiState.update { it.copy(isSuccess = true) }
                onSuccess()
            }
        }
    }
}

