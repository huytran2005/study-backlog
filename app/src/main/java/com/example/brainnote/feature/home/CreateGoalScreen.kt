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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
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
fun CreateGoalScreen(
    onBackClick: () -> Unit,
    onSaveClick: (title: String, description: String, startDate: String, endDate: String, status: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Đang thực hiện") }
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            FormTopAppBar(
                onBackClick = onBackClick,
                primaryColor = Color(0xFFF1C40F)
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
                title = "Mục tiêu",
                subtitle = "Theo dõi tiến độ và mục tiêu",
                icon = Icons.Default.Star,
                primaryColor = Color(0xFFF1C40F),
                backgroundColor = Color(0xFFFEF9E7)
            )
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Goal Title Input
            Text(text = "Tên mục tiêu", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Nhập tên mục tiêu truyền cảm hứng...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF1C40F),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description Input
            Text(text = "Mô tả", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Mô tả chi tiết về mục tiêu của bạn...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF1C40F),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dates input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Ngày bắt đầu", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        placeholder = { Text("Chọn ngày") },
                        leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF1C40F),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Ngày kết thúc", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        placeholder = { Text("Chọn ngày") },
                        leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF1C40F),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Status
            Text(text = "Trạng thái", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E1E1E))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val statuses = listOf(
                    Triple("Chưa bắt đầu", Color(0xFFF1F5F9), Color(0xFF79747E)),
                    Triple("Đang thực hiện", Color(0xFFFEF9E7), Color(0xFFC5A00C)),
                    Triple("Hoàn thành", Color(0xFFE6F7ED), Color(0xFF3DA36A))
                )
                statuses.forEach { item ->
                    val isSelected = status == item.first
                    val bg = if (isSelected) item.second else Color(0xFFF1F5F9)
                    val tc = if (isSelected) item.third else Color(0xFF79747E)
                    val borderCol = if (isSelected) item.third else Color.Transparent
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .border(1.dp, borderCol, RoundedCornerShape(12.dp))
                            .clickable { status = item.first }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.first,
                            color = tc,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Suggestion Box
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 Gợi ý",
                        color = Color(0xFFB45309),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Mục tiêu SMART: Cụ thể, Đo lường được, Khả thi, Liên quan, Có thời hạn",
                        color = Color(0xFF92400E),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(36.dp))
            
            // Save Button
            Button(
                onClick = { onSaveClick(title, description, startDate, endDate, status) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1C40F)),
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
                    Text("Tạo mục tiêu", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
