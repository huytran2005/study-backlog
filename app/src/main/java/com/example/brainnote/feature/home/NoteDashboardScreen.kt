package com.example.brainnote.feature.home

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.brainnote.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.ui.draw.shadow
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

private fun parseIdea(obj: JSONObject): NoteCardData.Idea {
    return NoteCardData.Idea(
        title = obj.getString("title"),
        description = obj.getString("description"),
        footerText = obj.getString("footerText")
    )
}

private fun parseImageIdea(obj: JSONObject): NoteCardData.ImageIdea {
    return NoteCardData.ImageIdea(
        title = obj.getString("title"),
        description = obj.getString("description"),
        footerText = obj.getString("footerText")
    )
}

private fun parseShoppingList(obj: JSONObject): NoteCardData.ShoppingList {
    val arr = obj.getJSONArray("items")
    val items = List(arr.length()) { arr.getString(it) }
    return NoteCardData.ShoppingList(
        title = obj.getString("title"),
        items = items,
        footerText = obj.getString("footerText")
    )
}

private fun parseNestedTask(obj: JSONObject): NoteCardData.NestedTask {
    val arr = obj.getJSONArray("tasks")
    val tasks = List(arr.length()) { i ->
        val taskObj = arr.getJSONObject(i)
        val name = taskObj.getString("name")
        val subtaskArr = taskObj.getJSONArray("subtasks")
        val subtasks = List(subtaskArr.length()) { subtaskArr.getString(it) }
        Pair(name, subtasks)
    }
    return NoteCardData.NestedTask(
        title = obj.getString("title"),
        description = obj.optString("description", ""),
        tasks = tasks,
        footerText = obj.getString("footerText")
    )
}

fun jsonToNoteCardData(obj: JSONObject): NoteCardData? {
    return try {
        when (obj.getString("type")) {
            "Idea" -> parseIdea(obj)
            "ImageIdea" -> parseImageIdea(obj)
            "ShoppingList" -> parseShoppingList(obj)
            "NestedTask" -> parseNestedTask(obj)
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
fun CircularProgressRing(
    progress: Float,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 6.dp.toPx()
            // Track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Composable
fun NoteDashboardScreen(
    onTaskCardClick: (Int) -> Unit = {}
) {
    val notesList by NoteRepository.notes.collectAsState()
    val scrollState = rememberScrollState()
    val colors = getBentoColors()

    // Gentle star canvas background (cleaner and less distracting)
    val stars = remember {
        List(25) {
            Offset(
                x = (0..1000).random() / 1000f,
                y = (0..1000).random() / 1000f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        AnimatedGalaxyBackground()

        // Soft twinkling stars canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            stars.forEachIndexed { index, offset ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.25f),
                    radius = 0.8.dp.toPx(),
                    center = Offset(offset.x * w, offset.y * h)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. Header Layout matching the wireframe (Separated)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "XIN CHÀO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9E99A6)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Dũng",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Avatar Circle on the right with a blue/indigo gradient and letter "D"
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF5694F0), Color(0xFF2F49D1))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "D",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Hôm nay card with Play button (Separated)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.cardBg, RoundedCornerShape(24.dp))
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "☀️", fontSize = 14.sp)
                        Text(
                            text = "Hôm nay",
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                    }
                    // Yellow/orange badge: 🔥 7 ngày
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE5A93C).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE5A93C).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🔥", fontSize = 11.sp)
                            Text(
                                text = "7 ngày",
                                fontSize = 11.sp,
                                color = Color(0xFFFFB74D),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "2h 35m",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Thời gian học hôm nay",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Outlined Button with Play icon: ▷ Bắt đầu học
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onTaskCardClick(-1) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "▷",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bắt đầu học",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Row with two side-by-side cards: 7 ngày and Phiên gần nhất
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Card: Tổng 7 ngày
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF131538), RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFF212459), RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressRing(
                        progress = 0.60f,
                        color = Color(0xFF8B5CF6),
                        trackColor = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                        modifier = Modifier.size(68.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Tổng 7 ngày",
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2h 12m",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                // Right Card: Phiên gần nhất
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF081C1B), RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFF103632), RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressRing(
                        progress = 0.75f,
                        color = Color(0xFF05B187),
                        trackColor = Color(0xFF05B187).copy(alpha = 0.15f),
                        modifier = Modifier.size(68.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Phiên gần nhất",
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "45 phút",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Progress Card with days of the week indicators
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.cardBg, RoundedCornerShape(24.dp))
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Assignment,
                            contentDescription = null,
                            tint = Color(0xFF5694F0),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Tiến độ tuần",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = "70%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5694F0)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar with Purple -> Blue gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(Color(0xFF2C2638), RoundedCornerShape(5.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF8A60A8), Color(0xFF3B82F6))
                                ),
                                RoundedCornerShape(5.dp)
                              )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Days of the week row: T2, T3, T4, T5, T6, T7, CN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                    days.forEachIndexed { index, day ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = day,
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )

                            val isCompleted = index < 5 // T2 to T6 are completed (index 0 to 4)
                            if (isCompleted) {
                                // Completed circle with checkmark
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFF2F49D1), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            } else {
                                // Incomplete hollow circle with dash
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFF1E1C24), CircleShape)
                                        .border(1.dp, Color(0xFF2C2638), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "—",
                                        color = colors.textSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4b. Hoạt động tuần Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.cardBg, RoundedCornerShape(24.dp))
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Canvas(modifier = Modifier.size(18.dp)) {
                            val w = size.width
                            val h = size.height
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(0f, h * 0.6f)
                                lineTo(w * 0.22f, h * 0.6f)
                                lineTo(w * 0.38f, h * 0.2f)
                                lineTo(w * 0.54f, h * 0.8f)
                                lineTo(w * 0.7f, h * 0.4f)
                                lineTo(w * 0.82f, h * 0.6f)
                                lineTo(w, h * 0.6f)
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFFB388FF),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 2.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                        Text(
                            text = "Hoạt động tuần",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Chart row containing only the bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val heights = listOf(0.45f, 0.55f, 0.38f, 0.75f, 0.85f, 0f, 0f)
                    heights.forEach { barHeightFraction ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            if (barHeightFraction > 0f) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .fillMaxHeight(barHeightFraction)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0xFF8A60A8), Color(0xFF3B82F6))
                                            ),
                                            RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Labels row containing day texts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                    days.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Recent Notes Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "📒", fontSize = 16.sp)
                    Text(
                        text = "Ghi chú gần đây",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
                Text(
                    text = "Xem tất cả",
                    fontSize = 12.sp,
                    color = Color(0xFFB388FF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { }
                )
            }

            // Render Notes cards with side bar decoration exactly matching screenshot
            if (notesList.isNotEmpty()) {
                notesList.take(2).forEachIndexed { noteIndex, note ->
                    val accentColor = if (noteIndex == 0) Color(0xFFB388FF) else Color(0xFF00E676)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .background(colors.cardBg, RoundedCornerShape(12.dp))
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left accent bar
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(4.dp)
                                    .background(accentColor)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                val title = when (note) {
                                    is NoteCardData.Idea -> note.title
                                    is NoteCardData.ImageIdea -> note.title
                                    is NoteCardData.ShoppingList -> note.title
                                    is NoteCardData.NestedTask -> note.title
                                }
                                val desc = when (note) {
                                    is NoteCardData.Idea -> note.description
                                    is NoteCardData.ImageIdea -> note.description
                                    is NoteCardData.ShoppingList -> note.items.joinToString(", ")
                                    is NoteCardData.NestedTask -> note.description
                                }

                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = desc,
                                    fontSize = 12.sp,
                                    color = colors.textSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(140.dp)) // Spacing for bottom bar
        }
    }
}

private fun toggleGroup(
    group: String,
    subtasks: List<String>,
    clickedGroup: String
): Pair<String, List<String>> {
    if (group != clickedGroup) {
        return Pair(group, subtasks)
    }
    val isGroupChecked = group.startsWith("[x] ")
    val newGroup = if (isGroupChecked) group.substring(4) else "[x] $group"
    val newSubtasks = subtasks.map { sub ->
        if (isGroupChecked) {
            if (sub.startsWith("[x] ")) sub.substring(4) else sub
        } else {
            if (!sub.startsWith("[x] ")) "[x] $sub" else sub
        }
    }
    return Pair(newGroup, newSubtasks)
}

private fun toggleSubtask(
    group: String,
    subtasks: List<String>,
    clickedGroup: String,
    clickedSubtask: String
): Pair<String, List<String>> {
    if (group != clickedGroup) {
        return Pair(group, subtasks)
    }
    val newSubtasks = subtasks.map { sub ->
        if (sub == clickedSubtask) {
            if (sub.startsWith("[x] ")) sub.substring(4) else "[x] $sub"
        } else sub
    }
    return Pair(group, newSubtasks)
}

fun toggleChecklistItems(
    tasks: List<Pair<String, List<String>>>,
    clickedGroup: String,
    clickedSubtask: String?
): List<Pair<String, List<String>>> {
    return tasks.map { (group, subtasks) ->
        if (clickedSubtask == null) {
            toggleGroup(group, subtasks, clickedGroup)
        } else {
            toggleSubtask(group, subtasks, clickedGroup, clickedSubtask)
        }
    }
}
