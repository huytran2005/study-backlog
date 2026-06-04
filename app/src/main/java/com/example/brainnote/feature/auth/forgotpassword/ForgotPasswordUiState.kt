package com.example.brainnote.feature.auth.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    val isFormValid: Boolean
        get() = email.isNotEmpty() && emailError == null
}

