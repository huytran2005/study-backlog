package com.example.brainnote.feature.auth.register

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.brainnote.ui.theme.BrainNoteTheme
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // --- UI Validation Tests ---

    @Test
    fun registerScreen_rendersCorrectly() {
        composeTestRule.setContent {
            BrainNoteTheme {
                RegisterScreenContent(
                    uiState = RegisterUiState(),
                    onFullNameChange = {},
                    onEmailChange = {},
                    onPasswordChange = {},
                    onRegisterClick = {},
                    onBackClick = {}
                )
            }
        }

        // Labels and standalone texts can still be verified using onNodeWithText
        composeTestRule.onNodeWithText("Let's Register").assertIsDisplayed()
        
        // Editable fields verified using the newly added stable testTags
        composeTestRule.onNodeWithTag("Full NameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("EmailField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("PasswordField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RegisterButton").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun registerScreen_invalidEmail_showsErrorMessage() {
        composeTestRule.setContent {
            BrainNoteTheme {
                RegisterScreenContent(
                    uiState = RegisterUiState(email = "invalid-email", emailError = "Please enter a valid email address"),
                    onFullNameChange = {},
                    onEmailChange = {},
                    onPasswordChange = {},
                    onRegisterClick = {},
                    onBackClick = {}
                )
            }
        }

        // Error message text is rendered as a standalone Text node, so onNodeWithText is safe and correct
        composeTestRule.onNodeWithText("Please enter a valid email address").assertIsDisplayed()
    }

    @Test
    fun registerScreen_registerButtonDisabled_whenEmailInvalid() {
        val state = RegisterUiState(
            fullName = "John Doe",
            email = "invalid-email",
            password = "Password123",
            emailError = "Please enter a valid email address"
        )
        
        composeTestRule.setContent {
            BrainNoteTheme {
                RegisterScreenContent(
                    uiState = state,
                    onFullNameChange = {},
                    onEmailChange = {},
                    onPasswordChange = {},
                    onRegisterClick = {},
                    onBackClick = {}
                )
            }
        }

        // Button should be disabled because state.isFormValid is false
        composeTestRule.onNodeWithTag("RegisterButton").assertIsNotEnabled()
    }

    @Test
    fun registerScreen_registerButtonEnabled_whenFormValid() {
        val state = RegisterUiState(
            fullName = "John Doe",
            email = "user@example.com",
            password = "Password123",
            emailError = null
        )

        composeTestRule.setContent {
            BrainNoteTheme {
                RegisterScreenContent(
                    uiState = state,
                    onFullNameChange = {},
                    onEmailChange = {},
                    onPasswordChange = {},
                    onRegisterClick = {},
                    onBackClick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("RegisterButton").assertIsEnabled()
    }

    // --- User Interaction Tests ---

    @Test
    fun registerScreen_userInteraction_triggersCallbacks() {
        val emailState = androidx.compose.runtime.mutableStateOf("user@example.com")
        var registerClicked = false

        composeTestRule.setContent {
            BrainNoteTheme {
                RegisterScreenContent(
                    uiState = RegisterUiState(
                        fullName = "John Doe",
                        email = emailState.value,
                        password = "Password123"
                    ),
                    onFullNameChange = {},
                    onEmailChange = { emailState.value = it },
                    onPasswordChange = {},
                    onRegisterClick = { registerClicked = true },
                    onBackClick = {}
                )
            }
        }

        // performTextReplacement safely replaces text on the exact editable node
        composeTestRule.onNodeWithTag("EmailField").performTextReplacement("new@example.com")
        org.junit.Assert.assertEquals("new@example.com", emailState.value)

        // performClick safely executes on the exact Button node
        composeTestRule.onNodeWithTag("RegisterButton").performScrollTo().performClick()
        org.junit.Assert.assertTrue(registerClicked)
    }

    @Test
    fun registerScreen_loadingStateDisablesButton() {
        val state = RegisterUiState(
            fullName = "John Doe",
            email = "user@example.com",
            password = "Password123",
            isLoading = true
        )

        composeTestRule.setContent {
            BrainNoteTheme {
                RegisterScreenContent(
                    uiState = state,
                    onFullNameChange = {},
                    onEmailChange = {},
                    onPasswordChange = {},
                    onRegisterClick = {},
                    onBackClick = {}
                )
            }
        }

        // Button should be disabled during loading
        composeTestRule.onNodeWithTag("RegisterButton").assertIsNotEnabled()
    }
}
