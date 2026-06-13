package com.example.brainnote.feature.home

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NoteRepositoryTest {

    private lateinit var mockContext: Context
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        // Reset private state of the NoteRepository object using reflection
        val isInitializedField = NoteRepository::class.java.getDeclaredField("isInitialized")
        isInitializedField.isAccessible = true
        isInitializedField.set(NoteRepository, false)

        val notesField = NoteRepository::class.java.getDeclaredField("_notes")
        notesField.isAccessible = true
        val notesStateFlow = notesField.get(NoteRepository) as MutableStateFlow<List<NoteCardData>>
        notesStateFlow.value = emptyList()

        // Setup MockK mocks for Android Context and SharedPreferences
        mockContext = mockk(relaxed = true)
        mockPrefs = mockk(relaxed = true)
        mockEditor = mockk(relaxed = true)

        every { mockContext.applicationContext.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE) } returns mockPrefs
        every { mockPrefs.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
    }

    @Test
    fun `initialize with no saved notes loads default notes and saves them`() {
        every { mockPrefs.getString("notes_list", null) } returns null

        NoteRepository.initialize(mockContext)

        val currentNotes = NoteRepository.notes.value
        assertTrue(currentNotes.isNotEmpty())
        // Verify default notes contain at least one idea
        assertTrue(currentNotes.any { it is NoteCardData.Idea })

        // Verify saveNotesToPrefs was called to persist defaults
        verify { mockEditor.putString("notes_list", any()) }
        verify { mockEditor.apply() }
    }

    @Test
    fun `initialize with saved notes loads them correctly`() {
        val savedJson = JSONArray().apply {
            put(JSONObject().apply {
                put("type", "Idea")
                put("title", "Saved Title")
                put("description", "Saved Description")
                put("footerText", "Category: Personal")
            })
        }.toString()

        every { mockPrefs.getString("notes_list", null) } returns savedJson

        NoteRepository.initialize(mockContext)

        val currentNotes = NoteRepository.notes.value
        assertEquals(1, currentNotes.size)
        val note = currentNotes[0] as NoteCardData.Idea
        assertEquals("Saved Title", note.title)
        assertEquals("Saved Description", note.description)
        assertEquals("Category: Personal", note.footerText)
    }

    @Test
    fun `feature 1 - Quick Note creation and persistence`() {
        every { mockPrefs.getString("notes_list", null) } returns JSONArray().toString()
        NoteRepository.initialize(mockContext)

        val title = "Đọc sách mỗi ngày"
        val content = "Đọc ít nhất 10 trang sách lập trình hoặc phát triển bản thân"
        val category = "Cá nhân"

        val quickNote = NoteCardData.Idea(
            title = title,
            description = content,
            footerText = "Category: $category"
        )

        NoteRepository.addNote(quickNote)

        val notes = NoteRepository.notes.value
        assertEquals(1, notes.size)
        val savedNote = notes[0] as NoteCardData.Idea
        assertEquals(title, savedNote.title)
        assertEquals(content, savedNote.description)
        assertEquals("Category: Cá nhân", savedNote.footerText)

        // Verify it was saved to SharedPreferences
        verify { mockEditor.putString("notes_list", any()) }
    }

    @Test
    fun `feature 2 - Weekly Plan creation and verification`() {
        every { mockPrefs.getString("notes_list", null) } returns JSONArray().toString()
        NoteRepository.initialize(mockContext)

        val title = "Kế hoạch Tuần 24"
        val week = "08/06 - 14/06"
        val description = "Hoàn thành chương 3 môn Kiến trúc phần mềm"
        val mainGoal = "Thi thử đạt ít nhất 8 điểm"
        val priority = "Cao"

        val tasks = listOf(
            Pair(description.ifBlank { "Plan Details" }, listOf("Goal: $mainGoal"))
        )
        val weeklyPlan = NoteCardData.NestedTask(
            title = "$title ($week)",
            tasks = tasks,
            footerText = "Priority: $priority"
        )

        NoteRepository.addNote(weeklyPlan)

        val notes = NoteRepository.notes.value
        assertEquals(1, notes.size)
        val savedNote = notes[0] as NoteCardData.NestedTask
        assertEquals("Kế hoạch Tuần 24 (08/06 - 14/06)", savedNote.title)
        assertEquals(1, savedNote.tasks.size)
        assertEquals("Hoàn thành chương 3 môn Kiến trúc phần mềm", savedNote.tasks[0].first)
        assertEquals("Goal: Thi thử đạt ít nhất 8 điểm", savedNote.tasks[0].second[0])
        assertEquals("Priority: Cao", savedNote.footerText)
    }

    @Test
    fun `feature 3 - Goal creation and verification`() {
        every { mockPrefs.getString("notes_list", null) } returns JSONArray().toString()
        NoteRepository.initialize(mockContext)

        val title = "Đạt IELTS 7.5"
        val description = "Luyện đề Cam và học từ vựng mỗi ngày"
        val startDate = "15/06/2026"
        val endDate = "15/12/2026"
        val status = "Đang thực hiện"

        val tasks = listOf(
            Pair(description.ifBlank { "Goal Details" }, listOf("Start: $startDate", "End: $endDate"))
        )
        val goal = NoteCardData.NestedTask(
            title = title,
            tasks = tasks,
            footerText = "Status: $status"
        )

        NoteRepository.addNote(goal)

        val notes = NoteRepository.notes.value
        assertEquals(1, notes.size)
        val savedNote = notes[0] as NoteCardData.NestedTask
        assertEquals("Đạt IELTS 7.5", savedNote.title)
        assertEquals(1, savedNote.tasks.size)
        assertEquals("Start: 15/06/2026", savedNote.tasks[0].second[0])
        assertEquals("End: 15/12/2026", savedNote.tasks[0].second[1])
        assertEquals("Status: Đang thực hiện", savedNote.footerText)
    }

    @Test
    fun `feature 4 - Task creation and updating with checklist verification`() {
        every { mockPrefs.getString("notes_list", null) } returns JSONArray().toString()
        NoteRepository.initialize(mockContext)

        val title = "Nhiệm vụ làm bài tập lớn"
        val description = "Làm báo cáo phần thiết kế hệ thống"
        val dueDate = "20/06/2026"
        val priority = "Cao"
        val category = "Học tập"
        val checklist = listOf(
            Pair("Vẽ sơ đồ UML", listOf("Sơ đồ lớp", "Sơ đồ tuần tự")),
            Pair("Viết đặc tả", listOf("Đặc tả ca sử dụng"))
        )

        val task = NoteCardData.NestedTask(
            title = title,
            description = description,
            tasks = checklist,
            footerText = "Priority: $priority | Due: $dueDate | Category: $category"
        )

        // 1. Add Task
        NoteRepository.addNote(task)
        val notesAfterAdd = NoteRepository.notes.value
        assertEquals(1, notesAfterAdd.size)
        val savedTask = notesAfterAdd[0] as NoteCardData.NestedTask
        assertEquals(title, savedTask.title)
        assertEquals(description, savedTask.description)
        assertEquals(2, savedTask.tasks.size)
        assertEquals("Vẽ sơ đồ UML", savedTask.tasks[0].first)
        assertEquals("Sơ đồ lớp", savedTask.tasks[0].second[0])

        // 2. Update Task
        val updatedChecklist = checklist + Pair("Xem lại mã nguồn", emptyList())
        val updatedTask = task.copy(
            title = "Nhiệm vụ làm bài tập lớn (Cập nhật)",
            tasks = updatedChecklist
        )

        NoteRepository.updateNote(0, updatedTask)
        val notesAfterUpdate = NoteRepository.notes.value
        assertEquals(1, notesAfterUpdate.size)
        val savedUpdatedTask = notesAfterUpdate[0] as NoteCardData.NestedTask
        assertEquals("Nhiệm vụ làm bài tập lớn (Cập nhật)", savedUpdatedTask.title)
        assertEquals(3, savedUpdatedTask.tasks.size)
        assertEquals("Xem lại mã nguồn", savedUpdatedTask.tasks[2].first)
        assertTrue(savedUpdatedTask.tasks[2].second.isEmpty())
    }

    @Test
    fun `json serialization and deserialization works correctly for all note types`() {
        val idea = NoteCardData.Idea("Idea Title", "Idea Desc", "Idea Footer")
        val imageIdea = NoteCardData.ImageIdea("Img Title", "Img Desc", "Img Footer")
        val shoppingList = NoteCardData.ShoppingList("Shop Title", listOf("Item1", "Item2"), "Shop Footer")
        val nestedTask = NoteCardData.NestedTask(
            title = "Task Title",
            description = "Task Desc",
            tasks = listOf(
                Pair("Group1", listOf("Sub1", "Sub2")),
                Pair("Group2", emptyList())
            ),
            footerText = "Task Footer"
        )

        // Serialize to JSON
        val ideaJson = idea.toJson()
        val imageIdeaJson = imageIdea.toJson()
        val shoppingListJson = shoppingList.toJson()
        val nestedTaskJson = nestedTask.toJson()

        // Deserialize back and assert equality
        val deserializedIdea = jsonToNoteCardData(ideaJson) as NoteCardData.Idea
        assertEquals(idea.title, deserializedIdea.title)
        assertEquals(idea.description, deserializedIdea.description)
        assertEquals(idea.footerText, deserializedIdea.footerText)

        val deserializedImageIdea = jsonToNoteCardData(imageIdeaJson) as NoteCardData.ImageIdea
        assertEquals(imageIdea.title, deserializedImageIdea.title)
        assertEquals(imageIdea.description, deserializedImageIdea.description)
        assertEquals(imageIdea.footerText, deserializedImageIdea.footerText)

        val deserializedShoppingList = jsonToNoteCardData(shoppingListJson) as NoteCardData.ShoppingList
        assertEquals(shoppingList.title, deserializedShoppingList.title)
        assertEquals(shoppingList.items, deserializedShoppingList.items)
        assertEquals(shoppingList.footerText, deserializedShoppingList.footerText)

        val deserializedNestedTask = jsonToNoteCardData(nestedTaskJson) as NoteCardData.NestedTask
        assertEquals(nestedTask.title, deserializedNestedTask.title)
        assertEquals(nestedTask.description, deserializedNestedTask.description)
        assertEquals(nestedTask.footerText, deserializedNestedTask.footerText)
        assertEquals(nestedTask.tasks.size, deserializedNestedTask.tasks.size)
        assertEquals(nestedTask.tasks[0].first, deserializedNestedTask.tasks[0].first)
        assertEquals(nestedTask.tasks[0].second, deserializedNestedTask.tasks[0].second)
    }
}
