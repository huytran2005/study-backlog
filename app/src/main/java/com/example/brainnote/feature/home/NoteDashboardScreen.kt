package com.example.brainnote.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Polymorphic data model representing different note cards
sealed class NoteCardData {
    data class Idea(
        val title: String,
        val description: String,
        val footerText: String
    ) : NoteCardData()

    data class ImageIdea(
        val title: String,
        val description: String,
        val footerText: String
    ) : NoteCardData()

    data class ShoppingList(
        val title: String,
        val items: List<String>,
        val footerText: String
    ) : NoteCardData()

    data class NestedTask(
        val title: String,
        val tasks: List<Pair<String, List<String>>>,
        val footerText: String,
        val description: String = ""
    ) : NoteCardData()
}

fun NoteCardData.toJson(): JSONObject {
    val obj = JSONObject()
    when (this) {
        is NoteCardData.Idea -> {
            obj.put("type", "Idea")
            obj.put("title", title)
            obj.put("description", description)
            obj.put("footerText", footerText)
        }
        is NoteCardData.ImageIdea -> {
            obj.put("type", "ImageIdea")
            obj.put("title", title)
            obj.put("description", description)
            obj.put("footerText", footerText)
        }
        is NoteCardData.ShoppingList -> {
            obj.put("type", "ShoppingList")
            obj.put("title", title)
            val arr = JSONArray()
            items.forEach { arr.put(it) }
            obj.put("items", arr)
            obj.put("footerText", footerText)
        }
        is NoteCardData.NestedTask -> {
            obj.put("type", "NestedTask")
            obj.put("title", title)
            obj.put("description", description)
            val arr = JSONArray()
            tasks.forEach { (taskName, subtasks) ->
                val taskObj = JSONObject()
                taskObj.put("name", taskName)
                val subtaskArr = JSONArray()
                subtasks.forEach { subtaskArr.put(it) }
                taskObj.put("subtasks", subtaskArr)
                arr.put(taskObj)
            }
            obj.put("tasks", arr)
            obj.put("footerText", footerText)
        }
    }
    return obj
}

fun jsonToNoteCardData(obj: JSONObject): NoteCardData? {
    return try {
        when (obj.getString("type")) {
            "Idea" -> NoteCardData.Idea(
                title = obj.getString("title"),
                description = obj.getString("description"),
                footerText = obj.getString("footerText")
            )
            "ImageIdea" -> NoteCardData.ImageIdea(
                title = obj.getString("title"),
                description = obj.getString("description"),
                footerText = obj.getString("footerText")
            )
            "ShoppingList" -> {
                val arr = obj.getJSONArray("items")
                val items = List(arr.length()) { arr.getString(it) }
                NoteCardData.ShoppingList(
                    title = obj.getString("title"),
                    items = items,
                    footerText = obj.getString("footerText")
                )
            }
            "NestedTask" -> {
                val arr = obj.getJSONArray("tasks")
                val tasks = List(arr.length()) { i ->
                    val taskObj = arr.getJSONObject(i)
                    val name = taskObj.getString("name")
                    val subtaskArr = taskObj.getJSONArray("subtasks")
                    val subtasks = List(subtaskArr.length()) { subtaskArr.getString(it) }
                    Pair(name, subtasks)
                }
                NoteCardData.NestedTask(
                    title = obj.getString("title"),
                    description = obj.optString("description", ""),
                    tasks = tasks,
                    footerText = obj.getString("footerText")
                )
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}

object NoteRepository {
    private var isInitialized = false
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private val _notes = MutableStateFlow<List<NoteCardData>>(emptyList())
    val notes = _notes.asStateFlow()

    fun initialize(context: Context) {
        if (isInitialized) return
        sharedPreferences = context.applicationContext.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
        val savedNotesJson = sharedPreferences.getString("notes_list", null)
        if (savedNotesJson != null) {
            val list = mutableListOf<NoteCardData>()
            try {
                val arr = JSONArray(savedNotesJson)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    jsonToNoteCardData(obj)?.let { list.add(it) }
                }
                _notes.value = list
            } catch (e: Exception) {
                _notes.value = getDefaultNotes()
            }
        } else {
            _notes.value = getDefaultNotes()
            saveNotesToPrefs()
        }
        isInitialized = true
    }

    private fun saveNotesToPrefs() {
        if (!::sharedPreferences.isInitialized) return
        val arr = JSONArray()
        _notes.value.forEach { arr.put(it.toJson()) }
        sharedPreferences.edit().putString("notes_list", arr.toString()).apply()
    }

    fun addNote(note: NoteCardData) {
        _notes.value = _notes.value + note
        saveNotesToPrefs()
    }

    fun updateNote(index: Int, note: NoteCardData) {
        val currentList = _notes.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = note
            _notes.value = currentList
            saveNotesToPrefs()
        }
    }

    private fun getDefaultNotes(): List<NoteCardData> {
        return listOf(
            NoteCardData.Idea(
                title = "New Product\nIdea Design",
                description = "Create a modern SaaS application for tracking task items and managing student backlog items easily with AI.",
                footerText = "Interesting Idea"
            ),
            NoteCardData.ImageIdea(
                title = "Idea Layout Sketch",
                description = "Sketch layouts on paper first, then transfer them into high-fidelity Figma components with a soft pastel theme.",
                footerText = "Explore UI Kit"
            ),
            NoteCardData.ShoppingList(
                title = "🛒 Monthly Buy List",
                items = listOf("Notebooks", "Sticky Notes", "Drawing Pens", "Mechanical Pencil"),
                footerText = "Figma Community"
            ),
            NoteCardData.NestedTask(
                title = "Weekly Sub-tasks",
                description = "This is a sample task description.",
                tasks = listOf(
                    Pair("Preparation", listOf("Verify UI assets", "Design custom SVG graphics")),
                    Pair("Development", listOf("Setup Jetpack Compose", "Implement Canvas drawings"))
                ),
                footerText = "In Progress"
            )
        )
    }
}

@Composable
fun NoteDashboardScreen(
    onTaskCardClick: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val notesList by NoteRepository.notes.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4F8)) // Figma canvas color
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero card banner (Full width, flush to edges)
            HeroCard(
                title = "Amazing Journey!",
                subtitle = "You have successfully finished 5 notes",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Two-column Masonry / Staggered Layout (Padded)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Column (Even indexes)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    notesList.forEachIndexed { index, item ->
                        if (index % 2 == 0) {
                            RenderNoteCard(item, onTaskCardClick = { onTaskCardClick(index) })
                        }
                    }
                }

                // Right Column (Odd indexes)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    notesList.forEachIndexed { index, item ->
                        if (index % 2 == 1) {
                            RenderNoteCard(item, onTaskCardClick = { onTaskCardClick(index) })
                        }
                    }
                }
            }

            // Margin bottom to prevent content blocking by bottom bar
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

/**
 * Polymorphic card selector rendering the proper layout card per card data type.
 */
@Composable
fun RenderNoteCard(
    data: NoteCardData,
    onTaskCardClick: () -> Unit
) {
    when (data) {
        is NoteCardData.Idea -> {
            IdeaCard(
                title = data.title,
                description = data.description,
                footerText = data.footerText,
                modifier = Modifier.fillMaxWidth()
            )
        }
        is NoteCardData.ImageIdea -> {
            ImageIdeaCard(
                title = data.title,
                description = data.description,
                footerText = data.footerText,
                modifier = Modifier.fillMaxWidth()
            )
        }
        is NoteCardData.ShoppingList -> {
            ShoppingListCard(
                title = data.title,
                items = data.items,
                footerText = data.footerText,
                modifier = Modifier.fillMaxWidth()
            )
        }
        is NoteCardData.NestedTask -> {
            NestedTaskCard(
                title = data.title,
                description = data.description,
                tasks = data.tasks,
                footerText = data.footerText,
                modifier = Modifier.fillMaxWidth(),
                onClick = onTaskCardClick
            )
        }
    }
}
