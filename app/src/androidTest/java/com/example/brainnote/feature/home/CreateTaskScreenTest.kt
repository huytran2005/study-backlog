package com.example.brainnote.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
                    onSaveClick = { _, _, _, _, _, _ -> }
                )
            }
        }

        // Verify Title and Subtitle Header
        composeTestRule.onNodeWithText("Nhiệm vụ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Checklist các việc cần hoàn thành").assertIsDisplayed()

        // Verify key text labels
        composeTestRule.onNodeWithText("Tiêu đề").assertExists()
        composeTestRule.onNodeWithText("Mô tả").assertExists()
        composeTestRule.onNodeWithText("Danh mục").assertExists()
        composeTestRule.onNodeWithText("Mức ưu tiên").assertExists()
        composeTestRule.onNodeWithText("Hạn hoàn thành").assertExists()
        composeTestRule.onNodeWithText("Checklist nhiệm vụ (Đa cấp)").assertExists()
    }

    @Test
    fun createTaskScreen_emptyTitle_showsValidationError() {
        composeTestRule.setContent {
            BrainNoteTheme {
                CreateTaskScreen(
                    onBackClick = {},
                    onSaveClick = { _, _, _, _, _, _ -> }
                )
            }
        }

        // Try to save without title
        composeTestRule.onNodeWithText("Tạo nhiệm vụ").performClick()

        // Assert error message shows up
        composeTestRule.onNodeWithText("Tiêu đề là bắt buộc").assertIsDisplayed()
    }

    @Test
    fun createTaskScreen_fillsAllFieldsAndSaves_triggersCallback() {
        var savedTitle = ""
        var savedDescription = ""
        var savedDueDate = ""
        var savedPriority = ""
        var savedCategory = ""
        var savedChecklist: List<Pair<String, List<String>>>? = null

        composeTestRule.setContent {
            BrainNoteTheme {
                CreateTaskScreen(
                    onBackClick = {},
                    onSaveClick = { title, description, dueDate, priority, category, checklist ->
                        savedTitle = title
                        savedDescription = description
                        savedDueDate = dueDate
                        savedPriority = priority
                        savedCategory = category
                        savedChecklist = checklist
                    }
                )
            }
        }

        // 1. Enter Title
        composeTestRule.onNodeWithText("Nhập tiêu đề nhiệm vụ...").performTextInput("Learn Integration Tests")

        // 2. Enter Description
        composeTestRule.onNodeWithText("Mô tả chi tiết nhiệm vụ...").performTextInput("Use Compose rules to test screens")

        // 3. Select Category (Work)
        composeTestRule.onNodeWithText("Work").performClick()

        // 4. Select Priority (Cao)
        composeTestRule.onNodeWithText("Cao").performClick()

        // 5. Enter Due Date
        composeTestRule.onNodeWithText("Chọn ngày").performTextInput("15/06/2026")

        // 6. Add Checklist Group
        composeTestRule.onNodeWithText("Thêm nhóm checklist...").performTextInput("Setup Environment")
        composeTestRule.onNodeWithContentDescription("Add Group").performClick()

        // Assert new group is displayed
        composeTestRule.onNodeWithText("Setup Environment").assertIsDisplayed()

        // 7. Add Subtask to that group
        composeTestRule.onNodeWithText("Thêm việc con...").performTextInput("Install dependency")
        composeTestRule.onNodeWithContentDescription("Add Subtask").performClick()

        // 8. Click Create Task (Tạo nhiệm vụ)
        composeTestRule.onNodeWithText("Tạo nhiệm vụ").performClick()

        // 9. Assert correct parameters passed to callback
        assertEquals("Learn Integration Tests", savedTitle)
        assertEquals("Use Compose rules to test screens", savedDescription)
        assertEquals("15/06/2026", savedDueDate)
        assertEquals("Cao", savedPriority)
        assertEquals("Work", savedCategory)
        
        // Assert checklist data structure
        val checklist = savedChecklist
        assertTrue(checklist != null)
        assertEquals(1, checklist!!.size)
        assertEquals("Setup Environment", checklist[0].first)
        assertEquals(1, checklist[0].second.size)
        assertEquals("Install dependency", checklist[0].second[0])
    }
}
