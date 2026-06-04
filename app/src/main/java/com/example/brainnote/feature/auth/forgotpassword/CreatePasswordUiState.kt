package com.example.brainnote.feature.auth.forgotpassword

/**
 * UI State container for the Create New Password screen.
 * Houses values of the password inputs, dynamic error logs, loading indicator, and calculated strength.
 */
data class CreatePasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val strength: PasswordStrength = PasswordStrength.WEAK,
    val isLoading: Boolean = false
) {
    /**
     * Computed property to determine if the entire form is valid.
     * Used directly to control the enabled/disabled status of the bottom CTA.
     */
    val isFormValid: Boolean
        get() = password.isNotEmpty() &&
                confirmPassword.isNotEmpty() &&
                passwordError == null &&
                confirmPasswordError == null &&
                password == confirmPassword
}

