package com.example.brainnote.feature.auth.register

/**
 * UI State container for the Register Screen.
 * Houses values of the registration inputs, validation error messages, and loading state.
 */
data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
) {
    /**
     * Computed property to determine if the entire form is valid.
     * Used directly to control the enabled/disabled status of the CTA button.
     */
    val isFormValid: Boolean
        get() = fullName.isNotEmpty() &&
                email.isNotEmpty() &&
                password.isNotEmpty() &&
                fullNameError == null &&
                emailError == null &&
                passwordError == null
}
