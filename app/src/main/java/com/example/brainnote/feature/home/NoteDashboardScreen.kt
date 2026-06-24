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
import androidx.compose.material3.Text
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
fun NoteDashboardScreen(
    onTaskCardClick: (Int) -> Unit = {}
) {
    val notesList by NoteRepository.notes.collectAsState()
    val scrollState = rememberScrollState()
    val colors = getBentoColors()

    // Slide-in and fade-in states for the top bar
    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isAnimated = true
    }

    val topBarAlpha by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = EaseOutCubic)
    )
    val topBarOffsetY by animateDpAsState(
        targetValue = if (isAnimated) 0.dp else (-16).dp,
        animationSpec = tween(durationMillis = 600, easing = EaseOutCubic)
    )

    // Infinite transitions for shifting background liquid mesh colors
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    // Infinite animation variables for the premium avatar and indicators
    val avatarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "avatar_rotation"
    )

    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_intensity"
    )

    // Twinkling stars position memory and animation
    val stars = remember {
        List(70) {
            Offset(
                x = (0..1000).random() / 1000f,
                y = (0..1000).random() / 1000f
            )
        }
    }

    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    // Gentle cloud floating animation (bobbing vertically)
    val cloudTranslationY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_translation"
    )
    
    // Dynamic color shifting for Blob 1 (Purple -> Indigo -> Fuchsia -> Blue -> Purple)
    val dynamicColor1 by infiniteTransition.animateColor(
        initialValue = colors.accentPurple.copy(alpha = 0.22f),
        targetValue = colors.accentPurple.copy(alpha = 0.22f),
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 16000
                colors.accentPurple.copy(alpha = 0.22f) at 0 with LinearEasing
                Color(0xFF6366F1).copy(alpha = 0.20f) at 4000 with LinearEasing
                Color(0xFFD946EF).copy(alpha = 0.22f) at 8000 with LinearEasing
                Color(0xFF3B82F6).copy(alpha = 0.20f) at 12000 with LinearEasing
                colors.accentPurple.copy(alpha = 0.22f) at 16000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dynamic_color1"
    )

    // Dynamic color shifting for Blob 2 (Gold -> Rose -> Orange -> Pink -> Gold)
    val dynamicColor2 by infiniteTransition.animateColor(
        initialValue = colors.accentGold.copy(alpha = 0.18f),
        targetValue = colors.accentGold.copy(alpha = 0.18f),
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 16000
                colors.accentGold.copy(alpha = 0.18f) at 0 with LinearEasing
                Color(0xFFF43F5E).copy(alpha = 0.18f) at 4000 with LinearEasing
                Color(0xFFF97316).copy(alpha = 0.18f) at 8000 with LinearEasing
                Color(0xFFEC4899).copy(alpha = 0.18f) at 12000 with LinearEasing
                colors.accentGold.copy(alpha = 0.18f) at 16000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dynamic_color2"
    )

    // Dynamic color shifting for Blob 3 (Green -> Cyan -> Emerald -> Teal -> Green)
    val dynamicColor3 by infiniteTransition.animateColor(
        initialValue = colors.accentGreen.copy(alpha = 0.16f),
        targetValue = colors.accentGreen.copy(alpha = 0.16f),
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 16000
                colors.accentGreen.copy(alpha = 0.16f) at 0 with LinearEasing
                Color(0xFF06B6D4).copy(alpha = 0.16f) at 4000 with LinearEasing
                Color(0xFF10B981).copy(alpha = 0.16f) at 8000 with LinearEasing
                Color(0xFF14B8A6).copy(alpha = 0.16f) at 12000 with LinearEasing
                colors.accentGreen.copy(alpha = 0.16f) at 16000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dynamic_color3"
    )

    // Dynamic color shifting for Blob 4 (Purple -> Violet -> Indigo -> Pink -> Purple)
    val dynamicColor4 by infiniteTransition.animateColor(
        initialValue = colors.accentPurple.copy(alpha = 0.20f),
        targetValue = colors.accentPurple.copy(alpha = 0.20f),
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 16000
                colors.accentPurple.copy(alpha = 0.20f) at 0 with LinearEasing
                Color(0xFF8B5CF6).copy(alpha = 0.18f) at 4000 with LinearEasing
                Color(0xFF6366F1).copy(alpha = 0.20f) at 8000 with LinearEasing
                Color(0xFFEC4899).copy(alpha = 0.18f) at 12000 with LinearEasing
                colors.accentPurple.copy(alpha = 0.20f) at 16000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dynamic_color4"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Galaxy Gradient Background with Floating Clouds & Mountain Overlay
        AnimatedGalaxyBackground()

        // 2. Starry Night Sky Canvas (Twinkling star dots + Shifting Aurora gradients overlay)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // A. Draw Twinkling Stars
            stars.forEachIndexed { index, offset ->
                val alphaFactor = if (index % 2 == 0) twinkle else (1.3f - twinkle)
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f * alphaFactor.coerceIn(0.1f, 1f)),
                    radius = if (index % 7 == 0) 1.5.dp.toPx() else 0.8.dp.toPx(),
                    center = Offset(offset.x * w, offset.y * h)
                )
            }

            // B. Shifting Aurora Blobs (Overlayed softly on top of stars)
            // 1. Shifting Purple-to-Blue Blob (Top Left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(dynamicColor1.copy(alpha = 0.12f), Color.Transparent),
                    radius = w * 0.85f,
                    center = Offset(w * 0.1f, h * 0.05f)
                ),
                radius = w * 0.85f,
                center = Offset(w * 0.1f, h * 0.05f)
            )

            // 2. Shifting Amber-to-Rose Blob (Top Right)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(dynamicColor2.copy(alpha = 0.10f), Color.Transparent),
                    radius = w * 0.8f,
                    center = Offset(w * 0.9f, h * 0.25f)
                ),
                radius = w * 0.8f,
                center = Offset(w * 0.9f, h * 0.25f)
            )

            // 3. Shifting Green-to-Teal Blob (Middle Left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(dynamicColor3.copy(alpha = 0.08f), Color.Transparent),
                    radius = w * 0.75f,
                    center = Offset(w * 0.2f, h * 0.5f)
                ),
                radius = w * 0.75f,
                center = Offset(w * 0.2f, h * 0.5f)
            )

            // 4. Shifting Indigo-to-Violet Blob (Bottom Right)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(dynamicColor4.copy(alpha = 0.10f), Color.Transparent),
                    radius = w * 0.8f,
                    center = Offset(w * 0.85f, h * 0.8f)
                ),
                radius = w * 0.8f,
                center = Offset(w * 0.85f, h * 0.8f)
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Premium Floating Top Bar Capsule (matching the CustomBottomNavigationBar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = topBarOffsetY)
                    .alpha(topBarAlpha)
                    .padding(vertical = 12.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp))
                    .background(colors.cardBg, RoundedCornerShape(24.dp))
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left profile details (Avatar + Greeting)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Vector Drawn High-End Abstract Portrait Avatar with Spinning Orbital Aura
                        Box(
                            modifier = Modifier.size(54.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // 1. Spinning dynamic gradient orbital arc
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val ringColor = colors.accentPurple
                                drawArc(
                                    color = ringColor.copy(alpha = glowIntensity),
                                    startAngle = avatarRotation,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = 2.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                                drawArc(
                                    color = colors.accentGold.copy(alpha = glowIntensity * 0.7f),
                                    startAngle = avatarRotation + 180f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = 2.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }

                            // 2. Main Profile Avatar with Custom Vector Portrait
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(4.dp, CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(colors.gradientStart, colors.gradientEnd)
                                        ),
                                        CircleShape
                                    )
                                    .border(1.5.dp, colors.cardBorder, CircleShape)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height

                                    // Draw neck shadow
                                    drawCircle(
                                        color = Color.Black.copy(alpha = 0.15f),
                                        radius = w * 0.35f,
                                        center = Offset(w * 0.5f, h * 0.88f)
                                    )
                                    
                                    // Face structure
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.92f),
                                        radius = w * 0.28f,
                                        center = Offset(w * 0.5f, h * 0.46f)
                                    )

                                    // Abstract geometric hair crown
                                    drawArc(
                                        color = Color(0xFF2C194D),
                                        startAngle = 180f,
                                        sweepAngle = 180f,
                                        useCenter = true,
                                        size = androidx.compose.ui.geometry.Size(w * 0.62f, h * 0.52f),
                                        topLeft = Offset(w * 0.19f, h * 0.18f)
                                    )

                                    // Premium minimalist abstract sunglasses/spectacles
                                    drawRect(
                                        color = Color(0xFF2C194D),
                                        topLeft = Offset(w * 0.3f, h * 0.43f),
                                        size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.11f)
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = "XIN CHÀO!",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentPurple,
                                letterSpacing = 1.4.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Người dùng",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Apple Bento Grid: Puzzle Row 1 (Today's time tall cell & stacked streak + max session stats)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tall Left Cell (Purple gradient)
                HeroBentoCell(
                    todayFocusTime = "2h 35m",
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                )

                // Stacked Right Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StreakBentoCell(
                        streakDays = 7,
                        modifier = Modifier.weight(1f)
                    )
                    StatsBentoCell(
                        value = "2h 12m",
                        label = "Lần lâu nhất",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Apple Bento Grid: Puzzle Row 2 (Integrated Action capsule cell)
            ActionBentoCell(
                text = "Bắt đầu phiên học mới",
                onClick = { onTaskCardClick(-1) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Apple Bento Grid: Puzzle Row 3 (Weekly target wide cell)
            WeeklyProgressBentoCell(
                currentHours = 14f,
                targetHours = 20f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            if (notesList.isNotEmpty()) {
                Text(
                    text = "GHI CHÚ CỦA BẠN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 12.dp)
                )

                notesList.forEachIndexed { index, note ->
                    when (note) {
                        is NoteCardData.Idea -> {
                            NoteBentoCell(
                                title = note.title,
                                description = note.description,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .padding(bottom = 12.dp)
                            )
                        }
                        is NoteCardData.ImageIdea -> {
                            NoteBentoCell(
                                title = note.title,
                                description = note.description,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .padding(bottom = 12.dp)
                            )
                        }
                        is NoteCardData.ShoppingList -> {
                            ShoppingListCard(
                                title = note.title,
                                items = note.items,
                                footerText = note.footerText,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            )
                        }
                        is NoteCardData.NestedTask -> {
                            // Do nothing: tasks are not shown in this section
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(160.dp))
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
