package com.example.brainnote.feature.auth.forgotpassword

data class VerificationCodeUiState(
    val code: String = "",
    val codeError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
) {
    val isFormValid: Boolean
        get() = code.length == 6 && codeError == null
}

