package com.example.brainnote.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.hasSetTextAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.brainnote.ui.theme.BrainNoteTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateWeeklyPlanScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun createWeeklyPlanScreen_rendersCorrectly() {
        composeTestRule.setContent {
            BrainNoteTheme {
                CreateWeeklyPlanScreen(
                    onBackClick = {},
                    onSaveClick = { _, _, _, _, _ -> }
                )
            }
        }

        // Verify header
        composeTestRule.onNodeWithText("Kế hoạch tuần").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lập kế hoạch cho những ngày sắp tới").assertIsDisplayed()

        // Verify inputs
        composeTestRule.onNodeWithText("Tiêu đề").assertExists()
        composeTestRule.onNodeWithText("Mô tả").assertExists()
        composeTestRule.onNodeWithText("Tuần").assertExists()
        composeTestRule.onNodeWithText("Mục tiêu chính").assertExists()
        composeTestRule.onNodeWithText("Mức ưu tiên").assertExists()
    }

    @Test
    fun createWeeklyPlanScreen_emptyTitle_buttonDisabled() {
        composeTestRule.setContent {
            BrainNoteTheme {
                CreateWeeklyPlanScreen(
                    onBackClick = {},
                    onSaveClick = { _, _, _, _, _ -> }
                )
            }
        }

        // Save button disabled when title is blank
        composeTestRule.onNodeWithText("Tạo kế hoạch").performScrollTo().assertIsNotEnabled()

        // Input title using hasSetTextAction
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Week 25 Plan")
        composeTestRule.onNodeWithText("Tạo kế hoạch").performScrollTo().assertIsEnabled()
    }

    @Test
    fun createWeeklyPlanScreen_fillsAllFieldsAndSaves_triggersCallback() {
        var savedTitle = ""
        var savedDescription = ""
        var savedWeek = ""
        var savedMainGoal = ""
        var savedPriority = ""

        composeTestRule.setContent {
            BrainNoteTheme {
                CreateWeeklyPlanScreen(
                    onBackClick = {},
                    onSaveClick = { title, description, week, mainGoal, priority ->
                        savedTitle = title
                        savedDescription = description
                        savedWeek = week
                        savedMainGoal = mainGoal
                        savedPriority = priority
                    }
                )
            }
        }

        // Find editable text fields
        val fields = composeTestRule.onAllNodes(hasSetTextAction())

        // Input values
        fields[0].performTextInput("Study Plan")
        fields[1].performTextInput("Complete chapters 4 & 5")
        
        // fields[2] is week (initial value is "Tuần này")
        // we can type text replacement or clear it and type:
        fields[2].performTextInput(" 15/06 - 21/06")
        
        fields[3].performTextInput("Pass midterms")

        // Select priority "Trung bình"
        composeTestRule.onNodeWithText("Trung bình").performScrollTo().performClick()

        // Click create plan
        composeTestRule.onNodeWithText("Tạo kế hoạch").performScrollTo().performClick()

        assertEquals("Study Plan", savedTitle)
        assertEquals("Complete chapters 4 & 5", savedDescription)
        assertEquals("Tuần này 15/06 - 21/06", savedWeek)
        assertEquals("Pass midterms", savedMainGoal)
        assertEquals("Trung bình", savedPriority)
    }
}
