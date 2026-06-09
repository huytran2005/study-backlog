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


import android.app.Activity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddNoteClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val primaryColor = Color(0xFF7445C8)
    val grayText = Color(0xFF79747E)

    // Scope immersive system bars to home screen destination and restore when leaving
    val view = LocalView.current
    val context = LocalContext.current
    DisposableEffect(view) {
        val activity = context as? Activity
        if (activity != null) {
            val windowInsetsController = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            if (activity != null) {
                val windowInsetsController = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

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
            when (selectedTab) {
                0 -> NoteDashboardScreen()
                1 -> FinishedNotesScreen()
                2 -> SearchNotesScreen()
                3 -> SettingsScreen()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchNotesScreen() {
    var searchQuery by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F4F8))
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Search Notes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E1E)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search your notes or tasks...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search"
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7445C8),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedLabelColor = Color(0xFF7445C8)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Recent Categories",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E1E1E)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Ideas", "Shopping", "Backlog", "Work").forEach { category ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEEE8F8))
                        .clickable { searchQuery = category }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        fontSize = 13.sp,
                        color = Color(0xFF7445C8),
                        fontWeight = FontWeight.Medium
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
