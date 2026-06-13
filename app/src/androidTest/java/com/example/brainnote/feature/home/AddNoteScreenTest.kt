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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddNoteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addNoteScreen_rendersCorrectly() {
        composeTestRule.setContent {
            BrainNoteTheme {
                AddNoteScreen(
                    onBackClick = {},
                    onSaveClick = { _, _, _ -> }
                )
            }
        }

        // Verify Header
        composeTestRule.onNodeWithText("Ghi chú nhanh").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ghi lại ý tưởng ngay lập tức").assertIsDisplayed()

        // Verify labels
        composeTestRule.onNodeWithText("Tiêu đề").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nội dung").assertIsDisplayed()
        composeTestRule.onNodeWithText("Danh mục").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gợi ý nhanh").assertIsDisplayed()
    }

    @Test
    fun addNoteScreen_emptyInputs_buttonDisabled() {
        composeTestRule.setContent {
            BrainNoteTheme {
                AddNoteScreen(
                    onBackClick = {},
                    onSaveClick = { _, _, _ -> }
                )
            }
        }

        // Initially both empty, so Save button should be disabled
        composeTestRule.onNodeWithText("Lưu ghi chú").assertIsNotEnabled()

        // Input into title, button should become enabled
        composeTestRule.onNodeWithText("Nhập tiêu đề để ghi chú...").performTextInput("My Title")
        composeTestRule.onNodeWithText("Lưu ghi chú").assertIsEnabled()
    }

    @Test
    fun addNoteScreen_fillsAllFieldsAndSaves_triggersCallback() {
        var savedTitle = ""
        var savedContent = ""
        var savedCategory = ""

        composeTestRule.setContent {
            BrainNoteTheme {
                AddNoteScreen(
                    onBackClick = {},
                    onSaveClick = { title, content, category ->
                        savedTitle = title
                        savedContent = content
                        savedCategory = category
                    }
                )
            }
        }

        // Fill inputs
        composeTestRule.onNodeWithText("Nhập tiêu đề để ghi chú...").performTextInput("Quick Thought")
        composeTestRule.onNodeWithText("Nhập nội dung ghi chú của bạn...").performTextInput("This is a quick idea description")

        // Select Category "Ý tưởng"
        composeTestRule.onNodeWithText("Ý tưởng").performClick()

        // Save
        composeTestRule.onNodeWithText("Lưu ghi chú").performClick()

        assertEquals("Quick Thought", savedTitle)
        assertEquals("This is a quick idea description", savedContent)
        assertEquals("Ý tưởng", savedCategory)
    }
}
