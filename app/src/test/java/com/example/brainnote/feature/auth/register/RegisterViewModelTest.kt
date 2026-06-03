package com.example.brainnote.feature.auth.register

import com.example.brainnote.feature.auth.login.FakeAuthRepository
import com.example.brainnote.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: RegisterViewModel
    private lateinit var fakeRepository: FakeAuthRepository

    @Before
    fun setup() {
        fakeRepository = FakeAuthRepository()
        viewModel = RegisterViewModel(fakeRepository)
    }

    // --- Email Validation Tests ---

    @Test
    fun `onEmailChanged with valid email updates state correctly`() {
        viewModel.onEmailChanged("user@example.com")
        assertEquals("user@example.com", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun `onEmailChanged with empty email clears error`() {
        viewModel.onEmailChanged("invalid")
        viewModel.onEmailChanged("")
        
        assertEquals("", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun `onEmailChanged with invalid email format sets emailError`() {
        viewModel.onEmailChanged("invalid-email")
        assertEquals("Please enter a valid email address", viewModel.uiState.value.emailError)
    }

    @Test
    fun `onEmailChanged with missing at symbol sets emailError`() {
        viewModel.onEmailChanged("userexample.com")
        assertEquals("Please enter a valid email address", viewModel.uiState.value.emailError)
    }

    @Test
    fun `onEmailChanged with missing domain sets emailError`() {
        viewModel.onEmailChanged("user@")
        assertEquals("Please enter a valid email address", viewModel.uiState.value.emailError)
    }

    @Test
    fun `onEmailChanged with domain but no TLD sets emailError`() {
        viewModel.onEmailChanged("user@example")
        assertEquals("Please enter a valid email address", viewModel.uiState.value.emailError)
    }

    @Test
    fun `onEmailChanged with domain and trailing dot but no TLD sets emailError`() {
        viewModel.onEmailChanged("user@example.")
        assertEquals("Please enter a valid email address", viewModel.uiState.value.emailError)
    }

    @Test
    fun `onEmailChanged with multiple at symbols sets emailError`() {
        viewModel.onEmailChanged("user@@example.com")
        assertEquals("Please enter a valid email address", viewModel.uiState.value.emailError)
    }

    @Test
    fun `onEmailChanged with leading trailing spaces sets emailError`() {
        viewModel.onEmailChanged(" user@example.com ")
        assertEquals("Please enter a valid email address", viewModel.uiState.value.emailError)
    }

    @Test
    fun `error cleared after entering a valid email`() {
        viewModel.onEmailChanged("invalid")
        assertEquals("Please enter a valid email address", viewModel.uiState.value.emailError)
        
        viewModel.onEmailChanged("user@example.com")
        assertNull(viewModel.uiState.value.emailError)
    }

    // --- Registration Flow Tests ---

    @Test
    fun `register blocked when email is invalid`() {
        viewModel.onFullNameChanged("John Doe")
        viewModel.onPasswordChanged("Password123")
        viewModel.onEmailChanged("invalid")
        
        var successCalled = false
        viewModel.registerUser { successCalled = true }

        assertFalse(viewModel.uiState.value.isFormValid)
        assertFalse(successCalled)
    }

    @Test
    fun `register blocked when required fields are empty`() {
        // Email is empty, so it's invalid form
        viewModel.onFullNameChanged("John Doe")
        viewModel.onPasswordChanged("Password123")
        
        var successCalled = false
        viewModel.registerUser { successCalled = true }

        assertFalse(viewModel.uiState.value.isFormValid)
        assertFalse(successCalled)
    }

    @Test
    fun `register allowed when form is valid`() = runTest {
        viewModel.onFullNameChanged("John Doe")
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("Password123")
        
        var successCalled = false
        viewModel.registerUser { successCalled = true }

        assertTrue(viewModel.uiState.value.isFormValid)
        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loading state changes correctly during registration`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.onFullNameChanged("John Doe")
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("Password123")
        
        fakeRepository.delayMillis = 1000L
        
        var successCalled = false
        viewModel.registerUser { successCalled = true }
        
        // At this point, the coroutine is suspended due to delay(1000)
        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(successCalled)

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(successCalled)
    }
}
