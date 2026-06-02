package com.example.brainnote.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Register Screen.
 * Encapsulates form inputs, validations, loading transactions, and keeps the UI code purely presentation-focused.
 */
class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Handles updates and basic validation to the user's Full Name.
     */
    fun onFullNameChanged(fullName: String) {
        _uiState.update { currentState ->
            val error = when {
                fullName.isEmpty() -> null // Preserve dynamic typing UX (don't flash error instantly)
                fullName.isBlank() -> "Name cannot be empty"
                else -> null
            }
            currentState.copy(
                fullName = fullName,
                fullNameError = error
            )
        }
    }

    /**
     * Handles updates and pattern validation to the user's Email address.
     */
    fun onEmailChanged(email: String) {
        _uiState.update { currentState ->
            val error = when {
                email.isEmpty() -> null
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email address"
                else -> null
            }
            currentState.copy(
                email = email,
                emailError = error
            )
        }
    }

    /**
     * Handles updates and security validation to the user's password.
     * Enforces:
     * - Minimum 8 characters
     * - At least one uppercase, one lowercase, and one number
     */
    fun onPasswordChanged(password: String) {
        _uiState.update { currentState ->
            val error = when {
                password.isEmpty() -> null
                password.length < 8 -> "Password must be at least 8 characters"
                !password.any { it.isUpperCase() } || !password.any { it.isLowerCase() } || !password.any { it.isDigit() } -> {
                    "Must contain uppercase, lowercase and number"
                }
                else -> null
            }
            currentState.copy(
                password = password,
                passwordError = error
            )
        }
    }

    /**
     * Triggers the user registration flow.
     * Simulates transaction delay and invokes completion callback on success.
     */
    fun registerUser(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulate standard API registration network delay
            delay(1500)
            
            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
