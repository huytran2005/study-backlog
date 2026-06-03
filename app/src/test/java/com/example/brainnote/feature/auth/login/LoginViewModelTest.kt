package com.example.brainnote.feature.auth.login

import com.example.brainnote.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel
    private lateinit var fakeRepository: FakeAuthRepository

    @Before
    fun setup() {
        fakeRepository = FakeAuthRepository()
        viewModel = LoginViewModel(fakeRepository)
    }

    @Test
    fun `updateEmail updates state correctly`() {
        viewModel.updateEmail("test@example.com")
        assertEquals("test@example.com", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun `updatePassword updates state correctly`() {
        viewModel.updatePassword("password123")
        assertEquals("password123", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `login with empty email sets emailError`() {
        viewModel.updateEmail("")
        viewModel.updatePassword("password123")
        
        var successCalled = false
        viewModel.login { successCalled = true }

        assertEquals("Email cannot be empty.", viewModel.uiState.value.emailError)
        assertFalse(successCalled)
    }

    @Test
    fun `login with invalid email sets emailError`() {
        viewModel.updateEmail("invalid-email")
        viewModel.updatePassword("password123")
        
        var successCalled = false
        viewModel.login { successCalled = true }

        assertEquals("Please enter a valid email address.", viewModel.uiState.value.emailError)
        assertFalse(successCalled)
    }

    @Test
    fun `login with empty password sets passwordError`() {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("")
        
        var successCalled = false
        viewModel.login { successCalled = true }

        assertEquals("Password cannot be empty.", viewModel.uiState.value.passwordError)
        assertFalse(successCalled)
    }

    @Test
    fun `login with short password sets passwordError`() {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("12345")
        
        var successCalled = false
        viewModel.login { successCalled = true }

        assertEquals("Password must be at least 6 characters.", viewModel.uiState.value.passwordError)
        assertFalse(successCalled)
    }

    @Test
    fun `login with valid input calls repository and onSuccess on success`() = runTest {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        
        var successCalled = false
        viewModel.login { successCalled = true }

        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `login with valid input handles loading state correctly`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        
        fakeRepository.delayMillis = 1000L
        
        var successCalled = false
        viewModel.login { successCalled = true }
        
        // At this point, the coroutine is suspended due to delay(1000)
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(successCalled)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(successCalled)
    }
    
    @Test
    fun `googleLogin updates loading state and calls onSuccess on success`() = runTest {
        var successCalled = false
        viewModel.googleLogin { successCalled = true }
        
        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
