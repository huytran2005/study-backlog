package com.example.brainnote.feature.auth.forgotpassword

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    private lateinit var viewModel: ForgotPasswordViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForgotPasswordViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onEmailChanged_emptyInput_setsErrorAndInvalid() {
        viewModel.onEmailChanged("")
        
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("Email cannot be empty", state.emailError)
        assertFalse(state.isFormValid)
    }

    @Test
    fun onEmailChanged_validInput_clearsErrorAndValid() {
        viewModel.onEmailChanged("test@example.com")
        
        val state = viewModel.uiState.value
        assertEquals("test@example.com", state.email)
        assertNull(state.emailError)
        assertTrue(state.isFormValid)
    }

    @Test
    fun submitEmail_validEmail_triggersLoadingAndSuccess() = runTest(testDispatcher) {
        viewModel.onEmailChanged("test@example.com")
        
        var isSuccessCalled = false
        viewModel.submitEmail(onSuccess = { isSuccessCalled = true })
        
        runCurrent()
        
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(isSuccessCalled)
        
        advanceUntilIdle()
        
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isSuccess)
        assertTrue(isSuccessCalled)
    }
}
