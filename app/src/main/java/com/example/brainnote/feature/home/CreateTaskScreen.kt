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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import android.app.DatePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar


private const val PRIORITY_LOW = "Thấp"
private const val PRIORITY_MEDIUM = "Trung bình"
private const val PRIORITY_HIGH = "Cao"

val TaskGroupsSaver = listSaver<SnapshotStateList<TaskGroupState>, List<Any>>(
    save = { list ->
        list.map { listOf(it.id, it.name, it.subtasks) }
    },
    restore = { restoredList ->
        val snapshotList = mutableStateListOf<TaskGroupState>()
        restoredList.forEach { item ->
            val id = item[0] as String
            val name = item[1] as String
            @Suppress("UNCHECKED_CAST")
            val subtasks = (item[2] as List<*>).map { it as String }
            snapshotList.add(TaskGroupState(id, name, subtasks))
        }
        snapshotList
    }
)

// State data structure matching the NestedTask data model
data class TaskGroupState(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val subtasks: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    taskIndex: Int? = null,
    onBackClick: () -> Unit,
    onSaveClick: (title: String, description: String, dueDate: String, priority: String, category: String, checklist: List<Pair<String, List<String>>>) -> Unit
) {
    val existingTask = remember(taskIndex) {
        taskIndex?.let { NoteRepository.notes.value.getOrNull(it) as? NoteCardData.NestedTask }
    }

    val initialPriority = existingTask?.let {
        val text = it.footerText
        if (text.contains("Priority: ")) {
            text.substringAfter("Priority: ").substringBefore(" |")
        } else PRIORITY_MEDIUM
    } ?: PRIORITY_MEDIUM

    val initialDueDate = existingTask?.let {
        val text = it.footerText
        if (text.contains("Due: ")) {
            text.substringAfter("Due: ").substringBefore(" |")
        } else ""
    } ?: ""

    val initialCategory = existingTask?.let {
        val text = it.footerText
        if (text.contains("Category: ")) {
            text.substringAfter("Category: ")
        } else "Study"
    } ?: "Study"

    var title by rememberSaveable { mutableStateOf(existingTask?.title ?: "") }
    var description by rememberSaveable { mutableStateOf(existingTask?.description ?: "") }
    var dueDate by rememberSaveable { mutableStateOf(initialDueDate) }
    var priority by rememberSaveable { mutableStateOf(initialPriority) }
    var selectedCategory by rememberSaveable { mutableStateOf(initialCategory) }
    
    // Nested Checklist Groups State - empty by default, populated if editing
    val taskGroups = rememberSaveable(saver = TaskGroupsSaver) { 
        val initialList = mutableStateListOf<TaskGroupState>()
        existingTask?.tasks?.forEach { (name, subtasks) ->
            initialList.add(TaskGroupState(name = name, subtasks = subtasks))
        }
        initialList
    }
    
    var newGroupTitle by rememberSaveable { mutableStateOf("") }
    val newSubtaskTitles = remember { mutableStateMapOf<String, String>() }
    
    var showError by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            FormTopAppBar(
                onBackClick = onBackClick,
                primaryColor = Color(0xFFD53F8C)
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
            FormHeader(
                title = "Nhiệm vụ",
                subtitle = "Checklist các việc cần hoàn thành",
                icon = Icons.AutoMirrored.Outlined.List,
                primaryColor = Color(0xFFD53F8C),
                backgroundColor = Color(0xFFFFF1F2)
            )
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Task Title Input
            TaskTitleInput(
                title = title,
                onTitleChange = { title = it },
                showError = showError,
                onHideError = { showError = false }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Description Input
            TaskDescriptionInput(
                description = description,
                onDescriptionChange = { description = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selection
            CategorySelection(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Priority Selection
            PrioritySelection(
                priority = priority,
                onPrioritySelected = { priority = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Due date
            DueDateInput(
                dueDate = dueDate,
                onDueDateChange = { dueDate = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Checklist Section
            Text(text = "Checklist nhiệm vụ (Đa cấp)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(12.dp))
            
            taskGroups.forEachIndexed { groupIndex, group ->
                val currentSubtext = newSubtaskTitles[group.id] ?: ""
                ChecklistGroupCard(
                    group = group,
                    groupIndex = groupIndex,
                    currentSubtext = currentSubtext,
                    onSubtextChange = { newSubtaskTitles[group.id] = it },
                    onDeleteGroup = { taskGroups.removeAt(groupIndex) },
                    onDeleteSubtask = { subIndex ->
                        val currentSubtasks = group.subtasks.toMutableList()
                        currentSubtasks.removeAt(subIndex)
                        taskGroups[groupIndex] = group.copy(subtasks = currentSubtasks)
                    },
                    onAddSubtask = {
                        if (currentSubtext.isNotBlank()) {
                            val currentSubtasks = group.subtasks.toMutableList()
                            currentSubtasks.add(currentSubtext.trim())
                            taskGroups[groupIndex] = group.copy(subtasks = currentSubtasks)
                            newSubtaskTitles[group.id] = ""
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Add checklist group input card
            AddChecklistGroupCard(
                newGroupTitle = newGroupTitle,
                onGroupTitleChange = { newGroupTitle = it },
                onAddGroup = {
                    if (newGroupTitle.isNotBlank()) {
                        taskGroups.add(TaskGroupState(name = newGroupTitle.trim()))
                        newGroupTitle = ""
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Save/Create Button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
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
                    val buttonText = if (taskIndex != null) "Lưu nhiệm vụ" else "Tạo nhiệm vụ"
                    Text(buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TaskTitleInput(
    title: String,
    onTitleChange: (String) -> Unit,
    showError: Boolean,
    onHideError: () -> Unit
) {
    Text(text = "Tiêu đề", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = title,
        onValueChange = { 
            onTitleChange(it) 
            if (it.isNotBlank()) onHideError()
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
}

@Composable
private fun TaskDescriptionInput(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Text(text = "Mô tả", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        placeholder = { Text("Mô tả chi tiết nhiệm vụ...") },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD53F8C),
            unfocusedBorderColor = Color(0xFFE0E0E0)
        ),
        minLines = 3,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CategorySelection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
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
                    .clickable { onCategorySelected(category) }
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
}

@Composable
private fun PrioritySelection(
    priority: String,
    onPrioritySelected: (String) -> Unit
) {
    Text(text = "Mức ưu tiên", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val priorities = listOf(
            Triple(PRIORITY_LOW, Color(0xFFE6F7ED), Color(0xFF3DA36A)),
            Triple(PRIORITY_MEDIUM, Color(0xFFFFF7ED), Color(0xFFEA580C)),
            Triple(PRIORITY_HIGH, Color(0xFFFFF1F2), Color(0xFFE11D48))
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
                    .clickable { onPrioritySelected(item.first) }
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
}

@Composable
private fun DueDateInput(
    dueDate: String,
    onDueDateChange: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            onDueDateChange(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Text(text = "Hạn hoàn thành", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
    Spacer(modifier = Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
    ) {
        OutlinedTextField(
            value = dueDate,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            placeholder = { Text("Chọn ngày") },
            leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color(0xFF1E1E1E),
                disabledBorderColor = Color(0xFFE0E0E0),
                disabledPlaceholderColor = Color.Gray,
                disabledLeadingIconColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ChecklistGroupCard(
    group: TaskGroupState,
    groupIndex: Int,
    currentSubtext: String,
    onSubtextChange: (String) -> Unit,
    onDeleteGroup: () -> Unit,
    onDeleteSubtask: (Int) -> Unit,
    onAddSubtask: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                IconButton(onClick = onDeleteGroup) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Group",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
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
                            onClick = { onDeleteSubtask(subIndex) },
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
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentSubtext,
                        onValueChange = onSubtextChange,
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
                        onClick = onAddSubtask,
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

@Composable
private fun AddChecklistGroupCard(
    newGroupTitle: String,
    onGroupTitleChange: (String) -> Unit,
    onAddGroup: () -> Unit
) {
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
                onValueChange = onGroupTitleChange,
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
                onClick = onAddGroup,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD53F8C)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("+ Nhóm", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
