package com.example.brainnote.feature.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerificationCodeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationCodeUiState())
    val uiState: StateFlow<VerificationCodeUiState> = _uiState.asStateFlow()

    fun onCodeChanged(code: String) {
        // Allow only digits and max 6 characters
        val filteredCode = code.filter { it.isDigit() }.take(6)
        
        _uiState.update { currentState ->
            val error = if (filteredCode.isEmpty() || filteredCode.length == 6) {
                null
            } else {
                "Code must be 6 digits."
            }
            currentState.copy(
                code = filteredCode,
                codeError = error
            )
        }
    }

    fun submitCode(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulate network delay
            delay(1500)
            
            _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            onSuccess()
        }
    }
}

