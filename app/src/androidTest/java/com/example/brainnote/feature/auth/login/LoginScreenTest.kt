package com.example.brainnote.feature.auth.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.brainnote.ui.theme.BrainNoteTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_rendersCorrectly() {
        composeTestRule.setContent {
            BrainNoteTheme {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithText("Let's Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
    }

    @Test
    fun loginScreen_emptyInput_showsErrorMessages() {
        composeTestRule.setContent {
            BrainNoteTheme {
                LoginScreen()
            }
        }

        // Click login without entering anything
        composeTestRule.onNodeWithText("Login").performClick()

        // Verify error messages
        composeTestRule.onNodeWithText("Email cannot be empty.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password cannot be empty.").assertIsDisplayed()
    }

    @Test
    fun loginScreen_invalidEmail_showsErrorMessage() {
        composeTestRule.setContent {
            BrainNoteTheme {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("invalid-email")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Login").performClick()

        composeTestRule.onNodeWithText("Please enter a valid email address.").assertIsDisplayed()
    }

    @Test
    fun loginScreen_shortPassword_showsErrorMessage() {
        composeTestRule.setContent {
            BrainNoteTheme {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("12345")
        composeTestRule.onNodeWithText("Login").performClick()

        composeTestRule.onNodeWithText("Password must be at least 6 characters.").assertIsDisplayed()
    }

    @Test
    fun loginScreen_successfulLogin_triggersCallback() {
        var onLoginSuccessCalled = false

        val fakeRepo = FakeAuthRepository()
        val viewModel = LoginViewModel(fakeRepo)

        composeTestRule.setContent {
            BrainNoteTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { onLoginSuccessCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        composeTestRule.onNodeWithText("Login").performClick()

        // Wait for coroutine to finish
        composeTestRule.waitForIdle()

        assertTrue(onLoginSuccessCalled)
    }

    @Test
    fun loginScreen_loadingState_disablesButton() {
        val fakeRepo = FakeAuthRepository().apply {
            delayMillis = 2000L // Add delay to observe loading state
        }
        val viewModel = LoginViewModel(fakeRepo)

        composeTestRule.setContent {
            BrainNoteTheme {
                LoginScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        
        // Initial state
        composeTestRule.onNodeWithText("Login").assertIsEnabled()

        // Trigger login
        composeTestRule.onNodeWithText("Login").performClick()

        // Wait for composition to update
        composeTestRule.waitForIdle()

        // Button should be disabled during loading
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()
    }
}
