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

/**
 * ViewModel for the Create New Password screen.
 * Encapsulates form state management, validations, and loading workflows.
 * Purely architecture-compliant: all business logic resides here.
 */
class CreatePasswordViewModel(
    private val passwordValidator: PasswordValidator = PasswordValidator(),
    private val authRepository: AuthRepository = DefaultAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePasswordUiState())
    val uiState: StateFlow<CreatePasswordUiState> = _uiState.asStateFlow()

    /**
     * Responds to changes in the new password field.
     * Triggers dynamic strength checking and validation rules.
     */
    fun onPasswordChanged(password: String) {
        _uiState.update { currentState ->
            val validationResult = passwordValidator.validate(password)
            // Prevent showing immediate error text on empty input to preserve user experience
            val error = if (password.isEmpty() || validationResult.isValid) null else validationResult.errorMessage
            val calculatedStrength = passwordValidator.calculateStrength(password)

            // Re-validate confirm password match if it was already filled
            val confirmError = if (currentState.confirmPassword.isEmpty() || passwordValidator.passwordsMatch(password, currentState.confirmPassword)) {
                null
            } else {
                "Passwords must match"
            }

            currentState.copy(
                password = password,
                passwordError = error,
                confirmPasswordError = confirmError,
                strength = calculatedStrength
            )
        }
    }

    /**
     * Responds to changes in the confirmation password field.
     * Triggers cross-field equality checks.
     */
    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { currentState ->
            val isMatching = passwordValidator.passwordsMatch(currentState.password, confirmPassword)
            val error = if (confirmPassword.isEmpty() || isMatching) null else "Passwords must match"

            currentState.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = error
            )
        }
    }

    /**
     * Executes the password creation workflow.
     * Performs a final validation pass, simulates API transaction latency,
     * and signals completion to the screen container.
     */
    fun createPassword(onSuccess: () -> Unit) {
        val state = _uiState.value
        // Safety check to ensure form is fully valid before submission
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val result = authRepository.resetPassword(state.password)
            
            _uiState.update { it.copy(isLoading = false) }
            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
}

