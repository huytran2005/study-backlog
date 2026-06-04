package com.example.brainnote.feature.auth.forgotpassword

import com.example.brainnote.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreatePasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CreatePasswordViewModel

    @Before
    fun setup() {
        viewModel = CreatePasswordViewModel()
    }

    @Test
    fun onPasswordChanged_validInput_updatesState() {
        viewModel.onPasswordChanged("Password123")
        
        val state = viewModel.uiState.value
        assertEquals("Password123", state.password)
        assertNull(state.passwordError)
        assertEquals(PasswordStrength.MEDIUM, state.strength)
    }

    @Test
    fun onPasswordChanged_invalidInput_setsError() {
        viewModel.onPasswordChanged("short")
        
        val state = viewModel.uiState.value
        assertEquals("short", state.password)
        assertEquals("At least 8 characters required.", state.passwordError)
        assertEquals(PasswordStrength.WEAK, state.strength)
    }

    @Test
    fun onConfirmPasswordChanged_matching_updatesState() {
        viewModel.onPasswordChanged("Password123")
        viewModel.onConfirmPasswordChanged("Password123")
        
        val state = viewModel.uiState.value
        assertEquals("Password123", state.confirmPassword)
        assertNull(state.confirmPasswordError)
        assertTrue(state.isFormValid)
    }

    @Test
    fun onConfirmPasswordChanged_mismatch_setsError() {
        viewModel.onPasswordChanged("Password123")
        viewModel.onConfirmPasswordChanged("Password124")
        
        val state = viewModel.uiState.value
        assertEquals("Password124", state.confirmPassword)
        assertEquals("Passwords must match", state.confirmPasswordError)
        assertFalse(state.isFormValid)
    }

    @Test
    fun createPassword_invalidForm_doesNotTrigger() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.onPasswordChanged("Password123")
        viewModel.onConfirmPasswordChanged("Password124") // Mismatch
        
        var successCalled = false
        viewModel.createPassword { successCalled = true }
        
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(successCalled)
    }

    @Test
    fun createPassword_validForm_triggersLoadingAndSuccess() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.onPasswordChanged("Password123")
        viewModel.onConfirmPasswordChanged("Password123")
        
        var successCalled = false
        viewModel.createPassword { successCalled = true }
        
        mainDispatcherRule.testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)
        
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(1500)
        mainDispatcherRule.testDispatcher.scheduler.runCurrent()
        
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(successCalled)
    }
}

