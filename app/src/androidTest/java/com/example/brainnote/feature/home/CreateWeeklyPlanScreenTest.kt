package com.example.brainnote.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
        composeTestRule.onNodeWithText("Tiêu đề").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mô tả").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tuần").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mục tiêu chính").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mức ưu tiên").assertIsDisplayed()
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
        composeTestRule.onNodeWithText("Tạo kế hoạch").assertIsNotEnabled()

        // Input title
        composeTestRule.onNodeWithText("Nhập tiêu đề để kế hoạch...").performTextInput("Week 25 Plan")
        composeTestRule.onNodeWithText("Tạo kế hoạch").assertIsEnabled()
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

        // Input values
        composeTestRule.onNodeWithText("Nhập tiêu đề để kế hoạch...").performTextInput("Study Plan")
        composeTestRule.onNodeWithText("Mô tả kế hoạch của bạn...").performTextInput("Complete chapters 4 & 5")
        composeTestRule.onNodeWithText("Chọn tuần").performTextInput("15/06 - 21/06")
        composeTestRule.onNodeWithText("Bạn muốn đạt được điều gì trong tuần này?").performTextInput("Pass midterms")

        // Select priority "Trung bình"
        composeTestRule.onNodeWithText("Trung bình").performClick()

        // Click create plan
        composeTestRule.onNodeWithText("Tạo kế hoạch").performClick()

        assertEquals("Study Plan", savedTitle)
        assertEquals("Complete chapters 4 & 5", savedDescription)
        assertEquals("15/06 - 21/06", savedWeek)
        assertEquals("Pass midterms", savedMainGoal)
        assertEquals("Trung bình", savedPriority)
    }
}
