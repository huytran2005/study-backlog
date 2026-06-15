package com.example.brainnote.feature.focus

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.brainnote.ui.theme.BrainNoteTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun focusScreen_rendersCorrectly_defaultState() {
        composeTestRule.setContent {
            BrainNoteTheme {
                FocusScreen()
            }
        }

        // Verify Header
        composeTestRule.onNodeWithText("Focus Mode").assertIsDisplayed()

        // Verify Default Timer display (25 minutes -> 25:00)
        composeTestRule.onNodeWithText("25:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("Focus Time").assertIsDisplayed()



        // Verify Buttons
        composeTestRule.onNodeWithContentDescription("Start").assertExists()
        composeTestRule.onNodeWithContentDescription("Reset timer").assertExists()
    }

    @Test
    fun focusScreen_stateTransitionsToBreak_andSkip() {
        composeTestRule.setContent {
            BrainNoteTheme {
                FocusScreen()
            }
        }

        // Click "Next" to skip focus and transition to break
        composeTestRule.onNodeWithText("Next").performClick()

        // Verify Break Time Header
        composeTestRule.onNodeWithText("Break Time").assertIsDisplayed()

        // Verify Default Break Timer (5 minutes -> 05:00)
        composeTestRule.onNodeWithText("05:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("Break Time").assertIsDisplayed()



        // Verify Action Buttons (Play/Pause, Reset, Skip)
        composeTestRule.onNodeWithContentDescription("Start").assertExists()
        composeTestRule.onNodeWithText("Skip").assertIsDisplayed()
    }
}
