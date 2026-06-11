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
        val footerText: String
    ) : NoteCardData()
}

object NoteRepository {
    private val _notes = MutableStateFlow<List<NoteCardData>>(
        listOf(
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
                tasks = listOf(
                    Pair("Preparation", listOf("Verify UI assets", "Design custom SVG graphics")),
                    Pair("Development", listOf("Setup Jetpack Compose", "Implement Canvas drawings"))
                ),
                footerText = "In Progress"
            )
        )
    )
    val notes = _notes.asStateFlow()

    fun addNote(note: NoteCardData) {
        _notes.value = _notes.value + note
    }
}

@Composable
fun NoteDashboardScreen(
    onTaskCardClick: () -> Unit = {}
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
                    notesList.filterIndexed { index, _ -> index % 2 == 0 }.forEach { item ->
                        RenderNoteCard(item, onTaskCardClick = onTaskCardClick)
                    }
                }

                // Right Column (Odd indexes)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    notesList.filterIndexed { index, _ -> index % 2 == 1 }.forEach { item ->
                        RenderNoteCard(item, onTaskCardClick = onTaskCardClick)
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
                tasks = data.tasks,
                footerText = data.footerText,
                modifier = Modifier.fillMaxWidth(),
                onClick = onTaskCardClick
            )
        }
    }
}
