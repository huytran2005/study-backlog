package com.example.brainnote.feature.auth.forgotpassword

/**
 * ValidationResult represents the output of a validation pass.
 *
 * @param isValid Indicates if the validation criteria was fully satisfied.
 * @param errorMessage Explanation of the failure, or null if valid.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

/**
 * PasswordValidator handles user validation logic for creating new passwords.
 * Kept purely functional, side-effect free, and easily unit testable.
 */
class PasswordValidator {

    /**
     * Validates if a password meets the required criteria:
     * - Minimum 8 characters
     * - Contains at least one uppercase letter (A-Z)
     * - Contains at least one lowercase letter (a-z)
     * - Contains at least one number (0-9)
     */
    fun validate(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(false, "Password cannot be empty.")
        }
        if (password.length < 8) {
            return ValidationResult(false, "At least 8 characters required.")
        }
        if (!password.any { it.isUpperCase() }) {
            return ValidationResult(false, "Must contain at least one uppercase letter.")
        }
        if (!password.any { it.isLowerCase() }) {
            return ValidationResult(false, "Must contain at least one lowercase letter.")
        }
        if (!password.any { it.isDigit() }) {
            return ValidationResult(false, "Must contain at least one number.")
        }
        return ValidationResult(true)
    }

    /**
     * Checks if the retyped password matches the original new password.
     */
    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    /**
     * Calculates the password strength on a 3-level scale (WEAK, MEDIUM, STRONG):
     * - WEAK: Length < 8, or doesn't meet all basic criteria.
     * - MEDIUM: Meets basic validation criteria.
     * - STRONG: Meets all basic validation criteria AND is longer (>= 12 chars) OR includes a special character.
     */
    fun calculateStrength(password: String): PasswordStrength {
        val basicValidation = validate(password)
        if (!basicValidation.isValid) {
            return PasswordStrength.WEAK
        }

        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val isVeryLong = password.length >= 12

        return if (isVeryLong || hasSpecialChar) {
            PasswordStrength.STRONG
        } else {
            PasswordStrength.MEDIUM
        }
    }
}

