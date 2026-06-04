package com.example.brainnote.feature.auth.forgotpassword

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class ForgotPasswordScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersCorrectly() {
        composeTestRule.setContent {
            ForgotPasswordScreen(onSuccess = {})
        }

        // Verify elements exist
        composeTestRule.onNodeWithText("Forgot Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter your email to receive a verification code").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Email AddressField").assertIsDisplayed()
        
        // Button disabled initially
        composeTestRule.onNodeWithText("Send Code").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun inputsAndValidation_showsErrors() {
        composeTestRule.setContent {
            ForgotPasswordScreen(onSuccess = {})
        }

        // Enter valid, then empty to trigger error
        composeTestRule.onNodeWithTag("Email AddressField").performTextInput("t")
        composeTestRule.onNodeWithTag("Email AddressField").performTextClearance()
        
        // Verify error
        composeTestRule.onNodeWithText("Email cannot be empty").assertIsDisplayed()
        
        // Button disabled
        composeTestRule.onNodeWithText("Send Code").assertIsNotEnabled()
    }

    @Test
    fun validForm_enablesButton() {
        composeTestRule.setContent {
            ForgotPasswordScreen(onSuccess = {})
        }

        composeTestRule.onNodeWithTag("Email AddressField").performTextInput("test@example.com")
        
        // Button enabled
        composeTestRule.onNodeWithText("Send Code").assertIsEnabled()
    }
}
