package com.example.brainnote.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.LayoutDirection

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import com.example.brainnote.feature.focus.FocusScreen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Flag


import android.app.Activity
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.animation.core.*
import androidx.compose.ui.res.painterResource
import com.example.brainnote.R
import androidx.compose.foundation.BorderStroke

private const val CATEGORY_PREFIX = "Category: "

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddNoteClick: () -> Unit = {},
    onTaskCardClick: (Int) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var activeFocusTaskIndex by remember { mutableStateOf<Int?>(null) }

    // Scope immersive system bars to home screen destination and restore when leaving
    val view = LocalView.current
    val context = LocalContext.current
    
    // Hide system bars permanently for a fullscreen app experience
    LaunchedEffect(Unit) {
        val activity = context as? Activity
        if (activity != null) {
            val windowInsetsController = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    Scaffold(
        bottomBar = {
            if (selectedTab != 1) {
                CustomBottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onAddNoteClick = onAddNoteClick
                )
            }
        },
        containerColor = if (selectedTab == 0 || selectedTab == 2) Color.Transparent else Color(0xFFF5F4F8)
    ) { innerPadding ->
        // We only apply innerPadding to tabs that need it, 
        // to allow NoteDashboardScreen's starry sky to draw behind the transparent bottom bar.
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val bottomPadding = innerPadding.calculateBottomPadding()
            when (selectedTab) {
                0 -> Box(modifier = Modifier.fillMaxSize()) {
                    NoteDashboardScreen(onTaskCardClick = onTaskCardClick)
                }
                1 -> Box(modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding)) {
                    FocusScreen(
                        activeTaskIndex = activeFocusTaskIndex,
                        onCloseClick = { 
                            selectedTab = 0 
                            activeFocusTaskIndex = null
                        }
                    )
                }
                2 -> Box(modifier = Modifier.fillMaxSize()) {
                    TasksScreen(
                        onTaskCardClick = onTaskCardClick,
                        onFocusClick = { index ->
                            activeFocusTaskIndex = index
                            selectedTab = 1
                        }
                    )
                }
                3 -> Box(modifier = Modifier.fillMaxSize().padding(bottom = bottomPadding)) {
                    SettingsScreen()
                }
            }
        }
    }
}

/**
 * Custom Shape that draws a top-rounded bar with a curved notch cutout in the center.
 */
class NotchShape(private val circleRadiusDp: Float, private val notchDepthDp: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val r = circleRadiusDp * density.density
            val d = notchDepthDp * density.density
            val corner = 28.dp.value * density.density

            moveTo(0f, corner)
            quadraticTo(0f, 0f, corner, 0f)

            // Draw line to the start of the notch
            val notchStart = cx - r - 10.dp.value * density.density
            lineTo(notchStart, 0f)

            // Curve into the notch
            quadraticTo(cx - r, 0f, cx - r * 0.7f, d * 0.5f)
            quadraticTo(cx, d * 1.15f, cx + r * 0.7f, d * 0.5f)
            quadraticTo(cx + r, 0f, cx + r + 10.dp.value * density.density, 0f)

            // Line to the top-right corner
            lineTo(w - corner, 0f)
            quadraticTo(w, 0f, w, corner)

            // Finish the bottom rectangle
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Modern Material 3 Floating Bottom Navigation Bar (No Notch)
 */
/**
 * Modern Material 3 Floating Bottom Navigation Bar (No Notch)
 */
@Composable
fun CustomBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onAddNoteClick: () -> Unit
) {
    val colors = getBentoColors()
    val gradientBrush =
        Brush.linearGradient(
        colors = listOf(colors.gradientStart, colors.gradientEnd),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    Surface(
        color = colors.cardBg,
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .navigationBarsPadding()
            .border(1.dp, colors.cardBorder, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTabItem(
                icon = Icons.Outlined.Home,
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            BottomTabItem(
                icon = Icons.Outlined.Timer,
                label = "Focus",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )

            // Center integrated Add FAB Button styled with bento gradient
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(gradientBrush, CircleShape)
                    .clip(CircleShape)
                    .clickable(onClick = onAddNoteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            BottomTabItem(
                icon = Icons.Outlined.CheckCircle,
                label = "Tasks",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                modifier = Modifier.weight(1f)
            )
            BottomTabItem(
                icon = Icons.Outlined.Settings,
                label = "Settings",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun RowScope.BottomTabItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = getBentoColors()
    val activeColor = colors.accentPurple
    val inactiveColor = colors.textSecondary.copy(alpha = 0.6f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) activeColor.copy(alpha = 0.15f) else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isSelected) colors.textPrimary else inactiveColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun FinishedNotesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4F8))
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Completed Notes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Here are the notes and lists you've fully completed.",
            fontSize = 14.sp,
            color = Color(0xFF79747E)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Render completed note card
        ShoppingListCard(
            title = "📚 Completed Course Materials",
            items = listOf("Jetpack Compose Basics", "Navigation component setup", "Figma Mockup Design"),
            footerText = "Finished",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

data class TaskStats(
    val totalCount: Int, val inProgressCount: Int, val completedCount: Int,
    val highCount: Int, val mediumCount: Int, val lowCount: Int, val noneCount: Int
)

private fun getTaskStats(taskItems: List<Pair<Int, NoteCardData.NestedTask>>): TaskStats {
    val isEmpty = taskItems.isEmpty()
    return TaskStats(
        totalCount = if (isEmpty) 12 else taskItems.size,
        inProgressCount = if (isEmpty) 5 else taskItems.count { !it.second.footerText.contains("Finished") },
        completedCount = if (isEmpty) 7 else taskItems.count { it.second.footerText.contains("Finished") },
        highCount = if (isEmpty) 2 else taskItems.count { it.second.footerText.contains("High") || it.second.footerText.contains("Cao") },
        mediumCount = if (isEmpty) 5 else taskItems.count { it.second.footerText.contains("Medium") || it.second.footerText.contains("Trung bình") },
        lowCount = if (isEmpty) 3 else taskItems.count { it.second.footerText.contains("Low") || it.second.footerText.contains("Thấp") },
        noneCount = 2
    )
}

@Composable
fun TasksScreen(
    onTaskCardClick: (Int) -> Unit = {},
    onFocusClick: (Int) -> Unit = {}
) {
    val notesList by NoteRepository.notes.collectAsState()
    val colors = getBentoColors()

    // Filter only NestedTask items and pair them with their original index
    val taskItems = notesList.mapIndexed { index, data -> Pair(index, data) }
        .filter { it.second is NoteCardData.NestedTask }
        .map { Pair(it.first, it.second as NoteCardData.NestedTask) }

    val stats = getTaskStats(taskItems)
    val totalCount = stats.totalCount
    val inProgressCount = stats.inProgressCount
    val completedCount = stats.completedCount
    val highCount = stats.highCount
    val mediumCount = stats.mediumCount
    val lowCount = stats.lowCount
    val noneCount = stats.noneCount

    var selectedCategory by remember { mutableStateOf("All") }

    // Twinkling stars position memory and animation
    val infiniteTransition = rememberInfiniteTransition(label = "tasks_bg")
    val stars = remember {
        List(40) {
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
        label = "tasks_twinkle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Background Starry Sky Image
        Image(
            painter = painterResource(id = R.drawable.home_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        // Twinkling Stars Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            stars.forEachIndexed { index, offset ->
                val alphaFactor = if (index % 2 == 0) twinkle else (1.3f - twinkle)
                drawCircle(
                    color = Color.White.copy(alpha = 0.45f * alphaFactor.coerceIn(0.1f, 1f)),
                    radius = 0.8.dp.toPx(),
                    center = Offset(offset.x * w, offset.y * h)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TasksHeader()

            CategoryTabs(
                taskItems = taskItems,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            // Statistics Card
            StatisticsSection(
                total = totalCount,
                inProgress = inProgressCount,
                completed = completedCount,
                high = highCount,
                medium = mediumCount,
                low = lowCount,
                none = noneCount
            )

            // Upcoming Tasks Section
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Tasks",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "See all",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accentPurple,
                        modifier = Modifier.clickable { }
                    )
                }

                if (taskItems.isEmpty()) {
                    MockTasksView()
                } else {
                    FilteredTasksView(
                        taskItems = taskItems,
                        selectedCategory = selectedCategory,
                        onTaskCardClick = onTaskCardClick,
                        onFocusClick = onFocusClick
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // padding for bottom bar
        }
    }
}

@Composable
fun TasksHeader() {
    val colors = getBentoColors()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "My Tasks",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Stay organized and get things done.",
                fontSize = 14.sp,
                color = colors.textSecondary
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = colors.accentPurple,
                modifier = Modifier.size(24.dp)
            )
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = colors.accentPurple,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CategoryTabs(
    taskItems: List<Pair<Int, NoteCardData.NestedTask>>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val colors = getBentoColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val dynamicCategories = taskItems.map { item ->
            val text = item.second.footerText
            if (text.contains(CATEGORY_PREFIX)) {
                text.substringAfter(CATEGORY_PREFIX).trim()
            } else "Study"
        }.distinct().sorted()

        val tabList = listOf(Triple("All Tasks", Icons.Outlined.CheckCircle, "All")) +
                dynamicCategories.map { cat -> Triple(cat, Icons.Outlined.Folder, cat) } +
                listOf(Triple("Completed", Icons.Outlined.CheckCircle, "Completed"))

        tabList.forEach { (label, icon, categoryKey) ->
            val isSelected = selectedCategory == categoryKey
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) colors.accentPurple else colors.cardBg)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp))
                    .clickable { onCategorySelected(categoryKey) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else colors.accentPurple,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = if (isSelected) Color.White else colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun MockTasksView() {
    val colors = getBentoColors()
    var mockChecklist by remember { mutableStateOf(listOf(
        Pair("Preparation", listOf("Verify UI assets", "Design custom SVG graphics")),
        Pair("Development", listOf("Setup Jetpack Compose", "Implement Canvas drawings"))
    )) }

    val totalSubtasks = mockChecklist.flatMap { it.second }.size
    val checkedSubtasks = mockChecklist.flatMap { it.second }.filter { it.startsWith("[x] ") }.size
    val progress = if (totalSubtasks > 0) checkedSubtasks.toFloat() / totalSubtasks.toFloat() else 0.5f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            TaskCardItem(
                state = TaskCardDisplayState(
                    title = "Weekly Sub-tasks",
                    description = "This is a sample task description.",
                    dueDate = "May 20, 2024",
                    category = "Design",
                    progress = progress,
                    priority = "Medium",
                    color = colors.accentPurple,
                    checklist = mockChecklist
                ),
                onToggleCheck = { clickedGroup, clickedSubtask ->
                    mockChecklist = toggleChecklistItems(mockChecklist, clickedGroup, clickedSubtask)
                },
                onClick = {}
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            TaskCardItem(
                state = TaskCardDisplayState(
                    title = "Project Presentation",
                    description = "Prepare slides for the client meeting.",
                    dueDate = "May 22, 2024",
                    category = "Work",
                    progress = 0.30f,
                    priority = "High",
                    color = Color(0xFFEF4444)
                ),
                onClick = {}
            )
        }
    }
}

@Composable
fun FilteredTasksView(
    taskItems: List<Pair<Int, NoteCardData.NestedTask>>,
    selectedCategory: String,
    onTaskCardClick: (Int) -> Unit,
    onFocusClick: (Int) -> Unit
) {
    val filteredTasks = if (selectedCategory == "All") {
        taskItems
    } else if (selectedCategory == "Completed") {
        taskItems.filter { it.second.footerText.contains("Finished") || it.second.footerText.contains("Completed") }
    } else {
        taskItems.filter { item ->
            val text = item.second.footerText
            val cat = if (text.contains(CATEGORY_PREFIX)) text.substringAfter(CATEGORY_PREFIX).trim() else "Study"
            cat == selectedCategory
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column (Even indexes)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredTasks.forEachIndexed { index, (originalIndex, task) ->
                if (index % 2 == 0) {
                    TaskCardRowItem(originalIndex, task, onTaskCardClick, onFocusClick)
                }
            }
        }

        // Right Column (Odd indexes)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filteredTasks.forEachIndexed { index, (originalIndex, task) ->
                if (index % 2 == 1) {
                    TaskCardRowItem(originalIndex, task, onTaskCardClick, onFocusClick)
                }
            }
        }
    }
}

@Composable
fun StatisticsSection(
    total: Int,
    inProgress: Int,
    completed: Int,
    high: Int,
    medium: Int,
    low: Int,
    none: Int
) {
    val colors = getBentoColors()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = colors.accentPurple.copy(alpha = 0.08f)),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    count = total.toString(),
                    label = "Total Tasks",
                    icon = Icons.Outlined.Assignment,
                    iconColor = colors.accentPurple,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color = colors.cardBorder,
                    thickness = 1.dp
                )

                StatItem(
                    count = inProgress.toString(),
                    label = "In Progress",
                    icon = Icons.Outlined.Timer,
                    iconColor = Color(0xFFFF9100),
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color = colors.cardBorder,
                    thickness = 1.dp
                )

                StatItem(
                    count = completed.toString(),
                    label = "Completed",
                    icon = Icons.Outlined.CheckCircle,
                    iconColor = Color(0xFF00E676),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = colors.cardBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Legend / Annotation Row (Priority Counts)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityLegendItem(label = "High", count = high, color = Color(0xFFEF4444))
                PriorityLegendItem(label = "Medium", count = medium, color = colors.accentPurple)
                PriorityLegendItem(label = "Low", count = low, color = Color(0xFF3B82F6))
                PriorityLegendItem(label = "None", count = none, color = colors.textSecondary)
            }
        }
    }
}

@Composable
fun PriorityLegendItem(label: String, count: Int, color: Color) {
    val colors = getBentoColors()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Text(
            text = "$label: $count",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = colors.textSecondary.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun StatItem(
    count: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = getBentoColors()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = count,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = colors.textSecondary
        )
    }
}

@Composable
fun TaskCardRowItem(
    originalIndex: Int,
    task: NoteCardData.NestedTask,
    onTaskCardClick: (Int) -> Unit,
    onFocusClick: (Int) -> Unit
) {
    val footer = task.footerText
    val priority = if (footer.contains("Priority: ")) {
        footer.substringAfter("Priority: ").substringBefore(" |").trim()
    } else "Medium"

    val dueDate = if (footer.contains("Due: ")) {
        footer.substringAfter("Due: ").substringBefore(" |").trim()
    } else if (footer.contains("Days: ")) {
        footer.substringAfter("Days: ").trim()
    } else "No Date"

    val category = if (footer.contains(CATEGORY_PREFIX)) {
        footer.substringAfter(CATEGORY_PREFIX).trim()
    } else ""

    val totalSubtasks = task.tasks.flatMap { it.second }.size
    val checkedSubtasks = task.tasks.flatMap { it.second }.filter { it.startsWith("[x] ") }.size
    val progress = if (totalSubtasks > 0) checkedSubtasks.toFloat() / totalSubtasks.toFloat() else 0.5f

    TaskCardItem(
        state = TaskCardDisplayState(
            title = task.title,
            description = task.description,
            dueDate = dueDate,
            category = category,
            progress = progress,
            priority = priority,
            color = Color(0xFF7C4DFF),
            checklist = task.tasks
        ),
        onToggleCheck = { clickedGroup, clickedSubtask ->
            val updatedList = toggleChecklistItems(task.tasks, clickedGroup, clickedSubtask)
            NoteRepository.updateNote(originalIndex, task.copy(tasks = updatedList))
        },
        onFocusClick = { onFocusClick(originalIndex) },
        onClick = { onTaskCardClick(originalIndex) }
    )
}

@Composable
fun TaskCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (checked) Color(0xFF7C4DFF) else Color(0xFF8A8A95)
    val backgroundColor = if (checked) Color(0xFF7C4DFF) else Color.Transparent

    Box(
        modifier = modifier
            .size(18.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(4.dp))
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

data class TaskCardDisplayState(
    val title: String,
    val description: String,
    val dueDate: String,
    val category: String,
    val progress: Float,
    val priority: String,
    val color: Color,
    val checklist: List<Pair<String, List<String>>> = emptyList()
)

@Composable
private fun TaskCardChecklistGroupRow(
    groupName: String,
    onToggleCheck: (String, String?) -> Unit
) {
    val isGroupChecked = groupName.startsWith("[x] ")
    val cleanGroupName = if (isGroupChecked) groupName.substring(4) else groupName
    val colors = getBentoColors()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        TaskCheckbox(
            checked = isGroupChecked,
            onCheckedChange = { onToggleCheck(groupName, null) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = cleanGroupName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isGroupChecked) colors.textSecondary.copy(alpha = 0.5f) else colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TaskCardChecklistSubtaskRow(
    groupName: String,
    subtask: String,
    onToggleCheck: (String, String?) -> Unit
) {
    val isSubChecked = subtask.startsWith("[x] ")
    val cleanSubName = if (isSubChecked) subtask.substring(4) else subtask
    val colors = getBentoColors()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 2.dp, bottom = 2.dp)
    ) {
        TaskCheckbox(
            checked = isSubChecked,
            onCheckedChange = { onToggleCheck(groupName, subtask) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = cleanSubName,
            fontSize = 13.sp,
            color = if (isSubChecked) colors.textSecondary.copy(alpha = 0.5f) else colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TaskCardChecklist(
    checklist: List<Pair<String, List<String>>>,
    onToggleCheck: (String, String?) -> Unit
) {
    checklist.forEach { (groupName, subtasks) ->
        TaskCardChecklistGroupRow(groupName, onToggleCheck)
        subtasks.forEach { subtask ->
            TaskCardChecklistSubtaskRow(groupName, subtask, onToggleCheck)
        }
    }
}

@Composable
fun TaskCardItem(
    state: TaskCardDisplayState,
    onToggleCheck: (String, String?) -> Unit = { _, _ -> },
    onFocusClick: () -> Unit = {},
    onClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val colors = getBentoColors()

    val priorityColor = when (state.priority.lowercase()) {
        "cao", "high" -> Color(0xFFEF4444)
        "trung bình", "medium" -> colors.accentPurple
        else -> Color(0xFF10B981)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = colors.accentPurple.copy(alpha = 0.08f)),
        colors = CardDefaults.cardColors(containerColor = colors.cardBg),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(state.color, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = state.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "Focus Mode",
                    tint = colors.accentPurple,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onFocusClick() }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                    tint = colors.textSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { isExpanded = !isExpanded }
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(priorityColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = state.priority,
                    color = priorityColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.description,
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    modifier = Modifier.clickable(onClick = onClick),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (state.checklist.isNotEmpty() && isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.cardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                TaskCardChecklist(
                    checklist = state.checklist,
                    onToggleCheck = onToggleCheck
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = state.dueDate,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (state.category.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Label,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = state.category,
                                fontSize = 11.sp,
                                color = colors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(2.5.dp))
                            .background(colors.cardBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(state.progress)
                                .background(priorityColor)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(state.progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4F8))
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsItem(label = "Edit Profile", icon = Icons.Outlined.Settings)
                Divider(color = Color(0xFFE0E0E0))
                SettingsItem(label = "Notification Settings", icon = Icons.Outlined.CheckCircle)
                Divider(color = Color(0xFFE0E0E0))
                SettingsItem(label = "Theme Preferences", icon = Icons.Outlined.Home)
                Divider(color = Color(0xFFE0E0E0))
                SettingsItem(label = "Privacy & Security", icon = Icons.Outlined.Search)
            }
        }
    }
}

@Composable
fun SettingsItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF7445C8)
            )
            Text(
                text = label,
                fontSize = 15.sp,
                color = Color(0xFF1E1E1E),
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = "›",
            fontSize = 20.sp,
            color = Color(0xFF9E9E9E)
        )
    }
}
