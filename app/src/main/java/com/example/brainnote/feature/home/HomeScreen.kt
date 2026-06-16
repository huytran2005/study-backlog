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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

private const val CATEGORY_PREFIX = "Category: "

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddNoteClick: () -> Unit = {},
    onTaskCardClick: (Int) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var activeFocusTaskIndex by remember { mutableStateOf<Int?>(null) }
    val primaryColor = Color(0xFF7445C8)

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
                    onTabSelected = { selectedTab = it }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab != 1) {
                LargeFAB(
                    primaryColor = primaryColor,
                    onClick = onAddNoteClick
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = Color(0xFFF5F4F8)
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    NoteDashboardScreen(onTaskCardClick = onTaskCardClick)
                }
                1 -> FocusScreen(
                    activeTaskIndex = activeFocusTaskIndex,
                    onCloseClick = { 
                        selectedTab = 0 
                        activeFocusTaskIndex = null
                    }
                )
                2 -> Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    TasksScreen(
                        onTaskCardClick = onTaskCardClick,
                        onFocusClick = { index ->
                            activeFocusTaskIndex = index
                            selectedTab = 1
                        }
                    )
                }
                3 -> Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
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
 * Reusable Large FAB component styled with a premium white outline and shadow.
 */
@Composable
fun LargeFAB(
    primaryColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .offset(y = 34.dp) // Half-embedded inside the notch
            .shadow(10.dp, CircleShape, spotColor = primaryColor.copy(alpha = 0.4f))
            .background(primaryColor, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add note",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }
}

/**
 * Bottom Navigation containing Home, Finished, Tasks, Settings with a gap in the center.
 */
@Composable
fun CustomBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val barShape = NotchShape(circleRadiusDp = 42f, notchDepthDp = 32f)

    Surface(
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 8.dp,
        shape = barShape,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .border(1.dp, Color.White.copy(alpha = 0.1f), barShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp),
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

            // Space holder for the FAB notch
            Spacer(modifier = Modifier.weight(0.9f))

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
    val activeColor = Color(0xFF7445C8)
    val inactiveColor = Color(0xFF8A8A95)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) activeColor else inactiveColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
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

@Composable
@Composable
fun TasksScreen(
    onTaskCardClick: (Int) -> Unit = {},
    onFocusClick: (Int) -> Unit = {}
) {
    val notesList by NoteRepository.notes.collectAsState()

    // Filter only NestedTask items and pair them with their original index
    val taskItems = notesList.mapIndexed { index, data -> Pair(index, data) }
        .filter { it.second is NoteCardData.NestedTask }
        .map { Pair(it.first, it.second as NoteCardData.NestedTask) }

    // Dynamic stats computation
    val totalCount = if (taskItems.isEmpty()) 12 else taskItems.size
    val inProgressCount = if (taskItems.isEmpty()) 5 else taskItems.filter { !it.second.footerText.contains("Finished") }.size
    val completedCount = if (taskItems.isEmpty()) 7 else taskItems.filter { it.second.footerText.contains("Finished") }.size

    // Priority stats
    val highCount = if (taskItems.isEmpty()) 2 else taskItems.filter { it.second.footerText.contains("High") || it.second.footerText.contains("Cao") }.size
    val mediumCount = if (taskItems.isEmpty()) 5 else taskItems.filter { it.second.footerText.contains("Medium") || it.second.footerText.contains("Trung bình") }.size
    val lowCount = if (taskItems.isEmpty()) 3 else taskItems.filter { it.second.footerText.contains("Low") || it.second.footerText.contains("Thấp") }.size
    val noneCount = 2

    var selectedCategory by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFC)) // Soft iOS background
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
                    color = Color(0xFF1E1E1E)
                )
                Text(
                    text = "See all",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7C4DFF),
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

@Composable
fun TasksHeader() {
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
                color = Color(0xFF1E1E1E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Stay organized and get things done.",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = Color(0xFF7C4DFF),
                modifier = Modifier.size(24.dp)
            )
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = Color(0xFF7C4DFF),
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
                    .background(if (isSelected) Color(0xFF7C4DFF) else Color(0xFFF1EFF7))
                    .clickable { onCategorySelected(categoryKey) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color(0xFF7C4DFF),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = if (isSelected) Color.White else Color(0xFF4A4A5A),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun MockTasksView() {
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
                    color = Color(0xFF7C4DFF),
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
                    color = Color(0xFF3B82F6)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.LightGray.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
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
                    iconColor = Color(0xFF7C4DFF),
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color = Color(0xFFF0F0F2),
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
                    color = Color(0xFFF0F0F2),
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
            HorizontalDivider(color = Color(0xFFF0F0F2), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Legend / Annotation Row (Priority Counts)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriorityLegendItem(label = "High", count = high, color = Color(0xFFEF4444))
                PriorityLegendItem(label = "Medium", count = medium, color = Color(0xFF7C4DFF))
                PriorityLegendItem(label = "Low", count = low, color = Color(0xFF3B82F6))
                PriorityLegendItem(label = "None", count = none, color = Color(0xFF8A8A95))
            }
        }
    }
}

@Composable
fun PriorityLegendItem(label: String, count: Int, color: Color) {
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
            color = Color.Gray
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
                color = Color(0xFF1E1E1E)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
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
fun TaskCardItem(
    state: TaskCardDisplayState,
    onToggleCheck: (String, String?) -> Unit = { _, _ -> },
    onFocusClick: () -> Unit = {},
    onClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val priorityColor = when (state.priority.lowercase()) {
        "cao", "high" -> Color(0xFFEF4444)
        "trung bình", "medium" -> Color(0xFF7C4DFF)
        else -> Color(0xFF10B981)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color.LightGray.copy(alpha = 0.1f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp)
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
                    color = Color(0xFF1E1E1E),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "Focus Mode",
                    tint = Color(0xFF7C4DFF),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onFocusClick() }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { isExpanded = !isExpanded }
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(priorityColor.copy(alpha = 0.1f))
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
                    color = Color.Gray,
                    modifier = Modifier.clickable(onClick = onClick),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (state.checklist.isNotEmpty() && isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(8.dp))

                state.checklist.forEach { (groupName, subtasks) ->
                    val isGroupChecked = groupName.startsWith("[x] ")
                    val cleanGroupName = if (isGroupChecked) groupName.substring(4) else groupName

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
                            color = if (isGroupChecked) Color.Gray else Color(0xFF1E1E1E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    subtasks.forEach { subtask ->
                        val isSubChecked = subtask.startsWith("[x] ")
                        val cleanSubName = if (isSubChecked) subtask.substring(4) else subtask

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
                                color = if (isSubChecked) Color.Gray else Color(0xFF4A4A5A),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
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
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = state.dueDate,
                            fontSize = 11.sp,
                            color = Color.Gray,
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
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = state.category,
                                fontSize = 11.sp,
                                color = Color.Gray,
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
                            .background(Color(0xFFE0E0E0))
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
                        color = Color.Gray
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
