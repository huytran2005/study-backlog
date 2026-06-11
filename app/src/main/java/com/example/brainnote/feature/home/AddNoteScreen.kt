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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    onBackClick: () -> Unit,
    onSaveClick: (title: String, content: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Cá nhân") }
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            FormTopAppBar(
                onBackClick = onBackClick,
                primaryColor = Color(0xFF7445C8)
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
                title = "Ghi chú nhanh",
                subtitle = "Ghi lại ý tưởng ngay lập tức",
                icon = Icons.Default.Info,
                primaryColor = Color(0xFF7445C8),
                backgroundColor = Color(0xFFEEE8F8)
            )
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Title Input
            Text(text = "Tiêu đề", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Nhập tiêu đề để ghi chú...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7445C8),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content Input
            Text(text = "Nội dung", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Nhập nội dung ghi chú của bạn...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7445C8),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                minLines = 5,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Format bar simulation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("B", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))
                Text("I", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                Text("U", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                Text("List", fontSize = 14.sp, color = Color.Gray)
                Text("Image", fontSize = 14.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Category
            Text(text = "Danh mục", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("Cá nhân", "Công việc", "Ý tưởng")
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    val bg = if (isSelected) Color(0xFFEEE8F8) else Color(0xFFF1F5F9)
                    val tc = if (isSelected) Color(0xFF7445C8) else Color(0xFF79747E)
                    val borderCol = if (isSelected) Color(0xFF7445C8) else Color.Transparent
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = tc,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(text = category, color = tc, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Suggestions
            Text(text = "Gợi ý nhanh", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Ý tưởng mới", "Việc cần nhớ", "Cảm hứng").forEach { hint ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable {
                                if (title.isEmpty()) title = hint else content += "\n- $hint"
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = hint, color = Color(0xFF79747E), fontSize = 13.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(36.dp))
            
            // Save Button
            Button(
                onClick = { onSaveClick(title, content, selectedCategory) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7445C8)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = title.isNotBlank()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Lưu ghi chú", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
