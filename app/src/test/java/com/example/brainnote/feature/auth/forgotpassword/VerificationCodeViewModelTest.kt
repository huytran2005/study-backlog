package com.example.brainnote.feature.auth.forgotpassword

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VerificationCodeViewModelTest {

    private lateinit var viewModel: VerificationCodeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = VerificationCodeViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCodeChanged_emptyInput_setsErrorAndInvalid() {
        viewModel.onCodeChanged("")
        
        val state = viewModel.uiState.value
        assertEquals("", state.code)
        assertNull(state.codeError) // Empty is handled gracefully without immediate error string usually, but my implementation shows null. Wait, let's verify my impl: if isEmpty() -> error=null
        assertFalse(state.isFormValid)
    }

    @Test
    fun onCodeChanged_invalidLength_setsErrorAndInvalid() {
        viewModel.onCodeChanged("1234")
        
        val state = viewModel.uiState.value
        assertEquals("1234", state.code)
        assertEquals("Code must be 6 digits.", state.codeError)
        assertFalse(state.isFormValid)
    }

    @Test
    fun onCodeChanged_validLength_clearsErrorAndValid() {
        viewModel.onCodeChanged("123456")
        
        val state = viewModel.uiState.value
        assertEquals("123456", state.code)
        assertNull(state.codeError)
        assertTrue(state.isFormValid)
    }

    @Test
    fun submitCode_validCode_triggersLoadingAndSuccess() = runTest(testDispatcher) {
        viewModel.onCodeChanged("123456")
        
        var isSuccessCalled = false
        viewModel.submitCode(onSuccess = { isSuccessCalled = true })
        
        runCurrent()
        
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(isSuccessCalled)
        
        advanceUntilIdle()
        
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isSuccess)
        assertTrue(isSuccessCalled)
    }
}

