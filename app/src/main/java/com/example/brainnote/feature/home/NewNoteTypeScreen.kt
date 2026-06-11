package com.example.brainnote.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewNoteTypeScreen(
    onBackClick: () -> Unit,
    onQuickNoteClick: () -> Unit,
    onWeeklyPlanClick: () -> Unit,
    onGoalClick: () -> Unit,
    onTaskClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "New Notes",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Color(0xFF1E1E1E),
                            modifier = Modifier.padding(end = 48.dp) // Offset back button to center the title
                        )
                    }
                },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onBackClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color(0xFF7445C8),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Back",
                            color = Color(0xFF7445C8),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "What Do You Want to\nNotes?",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 38.sp,
                color = Color(0xFF1E1E1E)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // 1. Ghi chú nhanh (Purple)
            NoteTypeOptionCard(
                title = "Ghi chú nhanh",
                subtitle = "Ghi lại ý tưởng ngay lập tức",
                icon = Icons.Outlined.Info, // Bulb representation
                backgroundColor = Color(0xFF7445C8),
                iconBgColor = Color(0xFF5A359D),
                onClick = onQuickNoteClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Kế hoạch tuần (Green)
            NoteTypeOptionCard(
                title = "Kế hoạch tuần",
                subtitle = "Lập kế hoạch cho những ngày sắp tới",
                icon = Icons.Outlined.ShoppingCart,
                backgroundColor = Color(0xFF50D38A),
                iconBgColor = Color(0xFF3DA36A),
                onClick = onWeeklyPlanClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Mục tiêu (Yellow)
            NoteTypeOptionCard(
                title = "Mục tiêu",
                subtitle = "Theo dõi tiến độ và mục tiêu",
                icon = Icons.Outlined.Star,
                backgroundColor = Color(0xFFF1C40F),
                iconBgColor = Color(0xFFC5A00C),
                onClick = onGoalClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Nhiệm vụ (Red)
            NoteTypeOptionCard(
                title = "Nhiệm vụ",
                subtitle = "Checklist các việc cần hoàn thành",
                icon = Icons.Outlined.List, // Clipboard checklist representation
                backgroundColor = Color(0xFFD53F8C),
                iconBgColor = Color(0xFFB03373),
                onClick = onTaskClick
            )
        }
    }
}

@Composable
fun NoteTypeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }
    }
}
