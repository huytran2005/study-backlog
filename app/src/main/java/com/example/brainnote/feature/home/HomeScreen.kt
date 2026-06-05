package com.example.brainnote.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainnote.R
import com.example.brainnote.ui.theme.BrainNoteTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * HomeScreen representing the empty state of Study Backlog.
 * Features centered illustration, custom pointing arrow, bottom navigation, and purple plus FAB.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddNoteClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val primaryColor = Color(0xFF6C43B8)
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
        containerColor = Color(0xFFEFE9F7)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFEFE9F7))
        ) {
            // Main content containing illustration and description
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Centered illustration
                Image(
                    painter = painterResource(id = R.drawable.home_iilustration),
                    contentDescription = "No notes available illustration",
                    modifier = Modifier
                        .size(240.dp)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = "Start Your Journey",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Every big step start with small step.\nNotes your first idea and start your journey!",
                    fontSize = 15.sp,
                    color = grayText,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Give space for the pointing arrow below description
                Spacer(modifier = Modifier.height(80.dp))
            }

            // Curved Arrow pointing to the FAB in the bottom-center
            CurvedArrowPointingToFab(
                modifier = Modifier.fillMaxSize(),
                color = primaryColor
            )
        }
    }
}

/**
 * A custom canvas composable drawing a curved dashed arrow pointing towards the FAB.
 */
@Composable
fun CurvedArrowPointingToFab(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Start point near the bottom right of the text/description
        val startX = w * 0.65f
        val startY = h * 0.68f

        // End point pointing towards the FAB position (centered bottom)
        val endX = w * 0.53f
        val endY = h * 0.84f

        // Control point to create a nice curve curving to the right and down
        val controlX = w * 0.75f
        val controlY = h * 0.78f

        val path = Path().apply {
            moveTo(startX, startY)
            quadraticTo(controlX, controlY, endX, endY)
        }

        // Draw curved dashed path
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                cap = StrokeCap.Round
            )
        )

        // Draw arrow head at (endX, endY) pointing towards the FAB.
        // We approximate the angle using tangent vector from control point to end point.
        val angle = atan2(endY - controlY, endX - controlX)
        val arrowLength = 10.dp.toPx()
        val arrowAngleRad = Math.toRadians(30.0)

        val arrowLeftX = endX - arrowLength * cos(angle - arrowAngleRad).toFloat()
        val arrowLeftY = endY - arrowLength * sin(angle - arrowAngleRad).toFloat()

        val arrowRightX = endX - arrowLength * cos(angle + arrowAngleRad).toFloat()
        val arrowRightY = endY - arrowLength * sin(angle + arrowAngleRad).toFloat()

        val arrowHeadPath = Path().apply {
            moveTo(endX, endY)
            lineTo(arrowLeftX, arrowLeftY)
            moveTo(endX, endY)
            lineTo(arrowRightX, arrowRightY)
        }

        drawPath(
            path = arrowHeadPath,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

/**
 * Reusable Large FAB component styled according to design specification.
 */
@Composable
fun LargeFAB(
    primaryColor: Color,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = primaryColor,
        contentColor = Color.White,
        modifier = Modifier
            .size(76.dp)
            .offset(y = 34.dp) // Enlarged and lowered slightly to sit perfectly on the rounded edge
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add note",
            modifier = Modifier.size(36.dp)
        )
    }
}

/**
 * Bottom Navigation containing Home, Finished, Search, Settings with a gap in the center for the FAB.
 */
@Composable
fun CustomBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    // Custom bottom navigation bar using Surface to support top rounded corners and proper padding
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Shunts above Android system navigation buttons
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left tabs: Home and Finished
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

            // Space holder for the FAB
            Spacer(modifier = Modifier.weight(0.8f))

            // Right tabs: Search and Settings
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
    val activeColor = Color(0xFF6C43B8)
    val inactiveColor = Color(0xFF9E9E9E)

    IconButton(
        onClick = onClick,
        modifier = modifier.height(56.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(28.dp) // Enlarged icon size
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp, // Text label
                color = if (isSelected) activeColor else inactiveColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPreview() {
    BrainNoteTheme {
        HomeScreen()
    }
}
