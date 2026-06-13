package com.example.brainnote.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
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
class CreateGoalScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun createGoalScreen_rendersCorrectly() {
        composeTestRule.setContent {
            BrainNoteTheme {
                CreateGoalScreen(
                    onBackClick = {},
                    onSaveClick = { _, _, _, _, _ -> }
                )
            }
        }

        // Verify header
        composeTestRule.onNodeWithText("Mục tiêu").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theo dõi tiến độ và mục tiêu").assertIsDisplayed()

        // Verify inputs
        composeTestRule.onNodeWithText("Tên mục tiêu").assertExists()
        composeTestRule.onNodeWithText("Mô tả").assertExists()
        composeTestRule.onNodeWithText("Ngày bắt đầu").assertExists()
        composeTestRule.onNodeWithText("Ngày kết thúc").assertExists()
        composeTestRule.onNodeWithText("Trạng thái").assertExists()
    }

    @Test
    fun createGoalScreen_emptyTitle_buttonDisabled() {
        composeTestRule.setContent {
            BrainNoteTheme {
                CreateGoalScreen(
                    onBackClick = {},
                    onSaveClick = { _, _, _, _, _ -> }
                )
            }
        }

        // Save button disabled when title is blank
        composeTestRule.onNodeWithText("Tạo mục tiêu").performScrollTo().assertIsNotEnabled()

        // Input title
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Graduate College")
        composeTestRule.onNodeWithText("Tạo mục tiêu").performScrollTo().assertIsEnabled()
    }

    @Test
    fun createGoalScreen_fillsAllFieldsAndSaves_triggersCallback() {
        var savedTitle = ""
        var savedDescription = ""
        var savedStartDate = ""
        var savedEndDate = ""
        var savedStatus = ""

        composeTestRule.setContent {
            BrainNoteTheme {
                CreateGoalScreen(
                    onBackClick = {},
                    onSaveClick = { title, description, startDate, endDate, status ->
                        savedTitle = title
                        savedDescription = description
                        savedStartDate = startDate
                        savedEndDate = endDate
                        savedStatus = status
                    }
                )
            }
        }

        // Find editable text fields
        val fields = composeTestRule.onAllNodes(hasSetTextAction())

        // Input values
        fields[0].performTextInput("Read 24 Books")
        fields[1].performTextInput("Read 2 books every month")
        
        // We skip date inputs because they are read-only and disabled in tests

        // Select status "Hoàn thành"
        composeTestRule.onNodeWithText("Hoàn thành").performScrollTo().performClick()

        // Click create goal
        composeTestRule.onNodeWithText("Tạo mục tiêu").performScrollTo().performClick()

        assertEquals("Read 24 Books", savedTitle)
        assertEquals("Read 2 books every month", savedDescription)
        assertEquals("", savedStartDate)
        assertEquals("", savedEndDate)
        assertEquals("Hoàn thành", savedStatus)
    }
}
