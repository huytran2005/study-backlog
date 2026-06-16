package com.example.brainnote.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.brainnote.ui.theme.BrainNoteTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateTaskScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun createTaskScreen_rendersCorrectly() {
        composeTestRule.setContent {
            BrainNoteTheme {
                CreateTaskScreen(
                    onBackClick = {},
                    onSaveClick = { _, _, _, _ -> }
                )
            }
        }

        // Verify Title and Subtitle Header
        composeTestRule.onNodeWithText("Nhiệm vụ hàng ngày").assertIsDisplayed()
        composeTestRule.onNodeWithText("Checklist các việc cần hoàn thành trong ngày").assertIsDisplayed()

        // Verify key text labels
        composeTestRule.onNodeWithText("Tiêu đề").assertExists()
        composeTestRule.onNodeWithText("Mô tả").assertExists()
        composeTestRule.onNodeWithText("Lặp lại vào (Chọn nhiều ngày)").assertExists()
        composeTestRule.onNodeWithText("Checklist nhiệm vụ (Đa cấp)").assertExists()
    }

    @Test
    fun createTaskScreen_emptyTitle_showsValidationError() {
        composeTestRule.setContent {
            BrainNoteTheme {
                CreateTaskScreen(
                    onBackClick = {},
                    onSaveClick = { _, _, _, _ -> }
                )
            }
        }

        // Try to save without title
        composeTestRule.onNodeWithText("Tạo nhiệm vụ").performScrollTo().performClick()

        // Assert error message shows up
        composeTestRule.onNodeWithText("Tiêu đề là bắt buộc").assertExists()
    }

    @Test
    fun createTaskScreen_fillsAllFieldsAndSaves_triggersCallback() {
        var savedTitle = ""
        var savedDescription = ""
        var savedDaysOfWeek: List<String>? = null
        var savedChecklist: List<Pair<String, List<String>>>? = null

        composeTestRule.setContent {
            BrainNoteTheme {
                CreateTaskScreen(
                    onBackClick = {},
                    onSaveClick = { title, description, daysOfWeek, checklist ->
                        savedTitle = title
                        savedDescription = description
                        savedDaysOfWeek = daysOfWeek
                        savedChecklist = checklist
                    }
                )
            }
        }

        // Find editable text fields
        val fields = composeTestRule.onAllNodes(hasSetTextAction())

        // 1. Enter Title
        fields[0].performTextInput("Learn Integration Tests")

        // 2. Enter Description
        fields[1].performTextInput("Use Compose rules to test screens")

        // 3. Select Days (T2 / Thứ 2, T3 / Thứ 3)
        composeTestRule.onNodeWithText("T2").performScrollTo().performClick()
        composeTestRule.onNodeWithText("T3").performScrollTo().performClick()

        // 4. Add Checklist Group (the group title is the next editable field)
        composeTestRule.onNodeWithText("Nhập nhóm nhiệm vụ chính...").performScrollTo().performTextInput("Setup Environment")
        composeTestRule.onNode(hasContentDescription("Add Group") or hasText("+ Nhóm")).performScrollTo().performClick()

        // Assert new group is displayed
        composeTestRule.onNodeWithText("Setup Environment").performScrollTo().assertExists()

        // 5. Add Subtask to that group
        composeTestRule.onNodeWithText("Thêm việc con...").performScrollTo().performTextInput("Install dependency")
        composeTestRule.onNodeWithContentDescription("Add Subtask").performScrollTo().performClick()

        // 6. Click Create Task (Tạo nhiệm vụ)
        composeTestRule.onNodeWithText("Tạo nhiệm vụ").performScrollTo().performClick()

        // 7. Assert correct parameters passed to callback
        assertEquals("Learn Integration Tests", savedTitle)
        assertEquals("Use Compose rules to test screens", savedDescription)
        assertTrue(savedDaysOfWeek != null)
        assertEquals(2, savedDaysOfWeek!!.size)
        assertTrue(savedDaysOfWeek!!.contains("Thứ 2"))
        assertTrue(savedDaysOfWeek!!.contains("Thứ 3"))
        
        // Assert checklist data structure
        val checklist = savedChecklist
        assertTrue(checklist != null)
        assertEquals(1, checklist!!.size)
        assertEquals("Setup Environment", checklist[0].first)
        assertEquals(1, checklist[0].second.size)
        assertEquals("Install dependency", checklist[0].second[0])
    }
}
