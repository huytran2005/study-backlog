package com.example.brainnote.feature.auth.forgotpassword

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class VerificationCodeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersCorrectly() {
        composeTestRule.setContent {
            VerificationCodeScreen(onBackClick = {}, onCodeVerified = {})
        }

        // Verify elements exist
        composeTestRule.onNodeWithText("Verify Code").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter the verification code sent to your email").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Verification CodeField").assertIsDisplayed()
        
        // Button disabled initially
        composeTestRule.onNodeWithTag("ContinueButton").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun inputsAndValidation_showsErrors() {
        composeTestRule.setContent {
            VerificationCodeScreen(onBackClick = {}, onCodeVerified = {})
        }

        // Enter invalid length
        composeTestRule.onNodeWithTag("Verification CodeField").performTextInput("123")
        
        // Verify error
        composeTestRule.onNodeWithText("Code must be 6 digits.").assertIsDisplayed()
        
        // Button still disabled
        composeTestRule.onNodeWithTag("ContinueButton").assertIsNotEnabled()
    }

    @Test
    fun validForm_enablesButton() {
        composeTestRule.setContent {
            VerificationCodeScreen(onBackClick = {}, onCodeVerified = {})
        }

        composeTestRule.onNodeWithTag("Verification CodeField").performTextInput("123456")
        
        // Button enabled
        composeTestRule.onNodeWithTag("ContinueButton").assertIsEnabled()
    }
}

