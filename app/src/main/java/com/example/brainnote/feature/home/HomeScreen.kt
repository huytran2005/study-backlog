package com.example.brainnote.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddNoteClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val primaryColor = Color(0xFF7445C8)
    val grayText = Color(0xFF79747E)

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            LargeFAB(
                primaryColor = primaryColor,
                onClick = onAddNoteClick
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = Color(0xFFF5F4F8)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            NoteDashboardScreen()
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
            .offset(y = 34.dp) // Pushed deeper down into the notch
            .shadow(10.dp, CircleShape, spotColor = primaryColor.copy(alpha = 0.4f))
            .background(primaryColor, CircleShape) // Inner purple FAB (no white border)
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
 * Bottom Navigation containing Home, Finished, Search, Settings with a gap in the center.
 */
@Composable
fun CustomBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val barShape = NotchShape(circleRadiusDp = 42f, notchDepthDp = 32f)

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        shape = barShape,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .border(1.dp, Color(0xFF0F172A).copy(alpha = 0.08f), barShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp, start = 8.dp, end = 8.dp),
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
                icon = Icons.Outlined.CheckCircle,
                label = "Finished",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )

            // Space holder for the FAB notch
            Spacer(modifier = Modifier.weight(0.9f))

            BottomTabItem(
                icon = Icons.Outlined.Search,
                label = "Search",
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
    val inactiveColor = Color(0xFF9E9E9E)

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
