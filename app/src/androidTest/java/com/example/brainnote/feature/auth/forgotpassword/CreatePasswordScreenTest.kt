package com.example.brainnote.feature.auth.forgotpassword

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class CreatePasswordScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenRendersCorrectly() {
        composeTestRule.setContent {
            CreatePasswordScreen(onBackClick = {}, onPasswordCreated = {})
        }

        // Verify title
        composeTestRule.onNodeWithText("Create a New\nPassword").assertIsDisplayed()
        
        // Verify text fields exist
        composeTestRule.onNodeWithTag("New PasswordField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Retype New PasswordField").assertIsDisplayed()
        
        // Verify button exists and is disabled initially
        composeTestRule.onNodeWithTag("Create PasswordButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Create PasswordButton").assertIsNotEnabled()
    }

    @Test
    fun inputsAndValidation_showsErrors() {
        composeTestRule.setContent {
            CreatePasswordScreen(onBackClick = {}, onPasswordCreated = {})
        }

        // Enter invalid password
        composeTestRule.onNodeWithTag("New PasswordField").performTextInput("weak")
        // Verify error text
        composeTestRule.onNodeWithText("At least 8 characters required.").assertIsDisplayed()
        
        // Enter mismatched confirm password
        composeTestRule.onNodeWithTag("Retype New PasswordField").performTextInput("weakk")
        composeTestRule.onNodeWithText("Passwords must match").assertIsDisplayed()
        
        // Button should still be disabled
        composeTestRule.onNodeWithTag("Create PasswordButton").assertIsNotEnabled()
    }

    @Test
    fun validForm_enablesButton() {
        composeTestRule.setContent {
            CreatePasswordScreen(onBackClick = {}, onPasswordCreated = {})
        }

        composeTestRule.onNodeWithTag("New PasswordField").performTextInput("StrongPass123!")
        composeTestRule.onNodeWithTag("Retype New PasswordField").performTextInput("StrongPass123!")
        
        composeTestRule.onNodeWithTag("Create PasswordButton").assertIsEnabled()
    }
}

