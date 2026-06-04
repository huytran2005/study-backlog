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

class VerificationCodeViewModel(
    private val authRepository: AuthRepository = DefaultAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationCodeUiState())
    val uiState: StateFlow<VerificationCodeUiState> = _uiState.asStateFlow()

    fun onCodeChanged(code: String) {
        _uiState.update { currentState ->
            val error = if (code.isNotEmpty() && code.length != 6) "Code must be 6 digits." else null
            currentState.copy(
                code = code,
                codeError = error
            )
        }
    }

    fun submitCode(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (!state.isFormValid || state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.verifyResetCode(state.code)
            _uiState.update { it.copy(isLoading = false) }
            if (result.isSuccess) {
                _uiState.update { it.copy(isSuccess = true) }
                onSuccess()
            }
        }
    }
}
