package com.example.brainnote.feature.auth.login

/**
 * LoginUiState holds the UI state for the Login Screen, representing text field contents,
 * loading state, and validate error messages.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
)
