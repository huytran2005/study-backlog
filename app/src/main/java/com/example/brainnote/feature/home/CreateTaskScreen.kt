package com.example.brainnote.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

// State data structure matching the NestedTask data model
data class TaskGroupState(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val subtasks: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onBackClick: () -> Unit,
    onSaveClick: (title: String, description: String, dueDate: String, priority: String, category: String, checklist: List<Pair<String, List<String>>>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Trung bình") }
    var selectedCategory by remember { mutableStateOf("Study") }
    
    // Nested Checklist Groups State
    val taskGroups = remember { 
        mutableStateListOf(
            TaskGroupState(name = "Preparation", subtasks = listOf("Verify UI assets", "Design custom SVG graphics")),
            TaskGroupState(name = "Development", subtasks = listOf("Setup Jetpack Compose"))
        ) 
    }
    
    var newGroupTitle by remember { mutableStateOf("") }
    val newSubtaskTitles = remember { mutableStateMapOf<String, String>() }
    
    var showError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onBackClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color(0xFFD53F8C),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Back",
                            color = Color(0xFFD53F8C),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF1F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD53F8C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.List,
                            contentDescription = "Task",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Nhiệm vụ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD53F8C)
                )
                Text(
                    text = "Checklist các việc cần hoàn thành",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Task Title Input
            Text(text = "Tiêu đề", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it 
                    if (it.isNotBlank()) showError = false
                },
                placeholder = { Text("Nhập tiêu đề nhiệm vụ...") },
                isError = showError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD53F8C),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    errorBorderColor = Color.Red
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            if (showError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tiêu đề là bắt buộc",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Description Input
            Text(text = "Mô tả", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Mô tả chi tiết nhiệm vụ...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD53F8C),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selection
            Text(text = "Danh mục", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val categories = listOf("Study", "Work", "Personal", "Health")
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    val bg = if (isSelected) Color(0xFFFFF1F2) else Color(0xFFF1F5F9)
                    val tc = if (isSelected) Color(0xFFD53F8C) else Color(0xFF79747E)
                    val borderCol = if (isSelected) Color(0xFFD53F8C) else Color.Transparent
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = category }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            color = tc,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Priority Selection
            Text(text = "Mức ưu tiên", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val priorities = listOf(
                    Triple("Thấp", Color(0xFFE6F7ED), Color(0xFF3DA36A)),
                    Triple("Trung bình", Color(0xFFFFF7ED), Color(0xFFEA580C)),
                    Triple("Cao", Color(0xFFFFF1F2), Color(0xFFE11D48))
                )
                priorities.forEach { item ->
                    val isSelected = priority == item.first
                    val bg = if (isSelected) item.second else Color(0xFFF1F5F9)
                    val tc = if (isSelected) item.third else Color(0xFF79747E)
                    val borderCol = if (isSelected) item.third else Color.Transparent
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                            .clickable { priority = item.first }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.first,
                            color = tc,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Due date
            Text(text = "Hạn hoàn thành", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                placeholder = { Text("Chọn ngày") },
                leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD53F8C),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Checklist Section (Nested Layout)
            Text(text = "Checklist nhiệm vụ (Đa cấp)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(12.dp))
            
            taskGroups.forEachIndexed { groupIndex, group ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // 1. Group Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .border(2.dp, Color(0xFFD53F8C), RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = group.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E1E)
                                )
                            }
                            IconButton(onClick = { taskGroups.removeAt(groupIndex) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Group",
                                    tint = Color.Red.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        
                        // 2. Sub-tasks list inside Group
                        Column(
                            modifier = Modifier.padding(start = 24.dp, top = 6.dp)
                        ) {
                            group.subtasks.forEachIndexed { subIndex, subtask ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .border(1.2.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = subtask, fontSize = 13.sp, color = Color.Gray)
                                    }
                                    IconButton(
                                        onClick = {
                                            val currentSubtasks = group.subtasks.toMutableList()
                                            currentSubtasks.removeAt(subIndex)
                                            taskGroups[groupIndex] = group.copy(subtasks = currentSubtasks)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Delete Subtask",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 3. Add Sub-task row under this specific Group
                            val currentSubtext = newSubtaskTitles[group.id] ?: ""
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = currentSubtext,
                                    onValueChange = { newSubtaskTitles[group.id] = it },
                                    placeholder = { Text("Thêm việc con...", fontSize = 12.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFD53F8C),
                                        unfocusedBorderColor = Color(0xFFE0E0E0)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (currentSubtext.isNotBlank()) {
                                            val currentSubtasks = group.subtasks.toMutableList()
                                            currentSubtasks.add(currentSubtext.trim())
                                            taskGroups[groupIndex] = group.copy(subtasks = currentSubtasks)
                                            newSubtaskTitles[group.id] = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD53F8C))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Subtask",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Add a New main Checklist Group input row
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFD53F8C).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newGroupTitle,
                        onValueChange = { newGroupTitle = it },
                        placeholder = { Text("Nhập nhóm nhiệm vụ chính...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD53F8C),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newGroupTitle.isNotBlank()) {
                                taskGroups.add(TaskGroupState(name = newGroupTitle.trim()))
                                newGroupTitle = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD53F8C)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+ Nhóm", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Save/Create Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        // Map taskGroups states back to List<Pair<String, List<String>>>
                        val checklistData = taskGroups.map { Pair(it.name, it.subtasks) }
                        onSaveClick(title, description, dueDate, priority, selectedCategory, checklistData)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD53F8C)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Tạo nhiệm vụ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
