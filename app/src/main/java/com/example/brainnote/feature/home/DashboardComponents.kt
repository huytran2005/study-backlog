package com.example.brainnote.feature.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color constants matching the design specification
val ThemePurple = Color(0xFF7C4DFF)
val ThemeGreen = Color(0xFF22C55E)
val ThemeOrange = Color(0xFFFB923C)
val ThemePink = Color(0xFFF43F5E)

val ThemePurpleBg = Color(0xFFFAF5FF)
val ThemeGreenBg = Color(0xFFF0FDF4)
val ThemeOrangeBg = Color(0xFFFFF7ED)
val ThemePinkBg = Color(0xFFFFF1F2)

val TextSlate = Color(0xFF64748B)
val TextDark = Color(0xFF0F172A)

/**
 * CategoryHeader representing a category with its title, description, and gradient icon.
 */
@Composable
fun CategoryHeader(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Icon in gradient circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.25f), color.copy(alpha = 0.05f))
                    )
                )
                .border(1.2.dp, color.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = TextSlate
            )
        }
    }
}

/**
 * Custom base card supporting premium SaaS shadows, borders, and Framer Motion-like hover/press translation.
 */
@Composable
fun BaseDashboardCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.(Boolean, Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val isActive = isHovered || isPressed

    // Framer motion style hover translation
    val translationY by animateDpAsState(
        targetValue = if (isActive) (-6).dp else 0.dp,
        animationSpec = tween(durationMillis = 250)
    )

    // Animated shadow elevation
    val elevationShadow by animateDpAsState(
        targetValue = if (isActive) 16.dp else 4.dp,
        animationSpec = tween(durationMillis = 250)
    )

    Box(
        modifier = modifier
            .offset(y = translationY)
            .shadow(
                elevation = elevationShadow,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFF0F172A).copy(alpha = 0.05f),
                spotColor = Color(0xFF0F172A).copy(alpha = 0.12f)
            )
            .background(Color.White, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFF0F172A).copy(alpha = 0.04f), RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        content(isHovered, isPressed)
    }
}

/**
 * Standard TaskCard with tags, descriptions, footer details, clock icon, and stacked avatars.
 */
@Composable
fun TaskCard(
    title: String,
    tags: List<String>,
    description: String,
    timeText: String,
    tagColors: Pair<Color, Color>, // (Bg, Text)
    modifier: Modifier = Modifier,
    avatarResList: List<Int> = emptyList(),
    badgeText: String? = null
) {
    BaseDashboardCard(modifier = modifier) { isHovered, _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header tags
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(tagColors.first)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tagColors.second
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Title
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Card Description
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = TextSlate,
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clock + Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ClockIcon(color = TextSlate)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timeText,
                        fontSize = 12.sp,
                        color = TextSlate,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Avatar Stacks
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Simple simulated dynamic avatars
                    Box(modifier = Modifier.width(52.dp), contentAlignment = Alignment.CenterStart) {
                        for (i in 0..2) {
                            val startPadding = i * 14
                            Box(
                                modifier = Modifier
                                    .padding(start = startPadding.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFD8B4FE).copy(alpha = 0.9f - (i * 0.2f)),
                                                Color(0xFF818CF8).copy(alpha = 0.9f - (i * 0.2f))
                                            )
                                        )
                                    )
                                    .border(1.5.dp, Color.White, CircleShape)
                            )
                        }
                    }

                    if (badgeText != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSlate
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * TaskCard with Purple/Lucide lightbulb background and illustration (Second Variation).
 */
@Composable
fun DecorativeTaskCard(
    title: String,
    description: String,
    bgBrush: Brush,
    illustration: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDashboardCard(modifier = modifier) { _, _ ->
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
            }

            // Bottom Right Illustration
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(90.dp)
            ) {
                illustration()
            }
        }
    }
}

/**
 * Card containing calendar, description, date footer, and an animated progress bar.
 */
@Composable
fun ProgressCard(
    title: String,
    badgeText: String,
    description: String,
    dateText: String,
    progress: Float, // value between 0f and 1f
    color: Color,
    tagColors: Pair<Color, Color>,
    modifier: Modifier = Modifier
) {
    BaseDashboardCard(modifier = modifier) { isHovered, _ ->
        // Animate the progress bar when card is hovered
        val animatedProgress by animateFloatAsState(
            targetValue = if (isHovered) progress + 0.08f else progress,
            animationSpec = tween(durationMillis = 300)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(tagColors.first)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = tagColors.second
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = TextSlate,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column {
                // Progress Label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CalendarIcon(color = TextSlate)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dateText,
                            fontSize = 12.sp,
                            color = TextSlate,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar Track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f))
                ) {
                    // Filled progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

/**
 * GoalCard with an embedded custom-drawn orange line chart inside the description/chart area.
 */
@Composable
fun GoalCard(
    title: String,
    badgeText: String,
    dateText: String,
    progress: Float,
    color: Color,
    tagColors: Pair<Color, Color>,
    chartContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    BaseDashboardCard(modifier = modifier) { isHovered, _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(tagColors.first)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = tagColors.second
                        )
                    }

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            // Mid section: Line Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(vertical = 4.dp)
            ) {
                chartContent()
            }

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateText,
                    fontSize = 11.sp,
                    color = TextSlate,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Tiến độ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }
    }
}

/**
 * ChecklistCard containing interactive checkboxes and optional illustration.
 */
@Composable
fun ChecklistCard(
    title: String,
    tags: List<String>,
    tagColors: Pair<Color, Color>,
    checklistItems: List<Pair<String, Boolean>>,
    illustration: @Composable (BoxScope.() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BaseDashboardCard(modifier = modifier) { _, _ ->
        var itemsState by remember { mutableStateOf(checklistItems) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(tagColors.first)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tagColors.second
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Middle section containing checklist + optional right illustration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsState.forEachIndexed { idx, pair ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val newList = itemsState.toMutableList()
                                    newList[idx] = Pair(pair.first, !pair.second)
                                    itemsState = newList
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (pair.second) ThemePink else Color(0xFFCBD5E1),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .background(
                                        color = if (pair.second) ThemePink else Color.Transparent,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (pair.second) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = "checked",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = pair.first,
                                fontSize = 13.sp,
                                color = if (pair.second) TextSlate else TextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (illustration != null) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .padding(start = 8.dp)
                    ) {
                        illustration()
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Deadline: 20 May, 2024",
                    fontSize = 11.sp,
                    color = TextSlate,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF1F2))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Hạn gấp",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThemePink
                    )
                }
            }
        }
    }
}

/**
 * Horizontal feature description box at the bottom of the screen.
 */
@Composable
fun FeatureItem(
    icon: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF0F172A).copy(alpha = 0.03f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSlate
            )
        }
    }
}

// Minimalist Custom Vector Icons replacing standard icons
@Composable
fun ClockIcon(color: Color) {
    Canvas(modifier = Modifier.size(14.dp)) {
        val r = size.width / 2
        drawCircle(
            color = color,
            radius = r,
            style = Stroke(width = dpToPx(1.5f))
        )
        drawLine(
            color = color,
            start = Offset(r, r),
            end = Offset(r, r - r * 0.5f),
            strokeWidth = dpToPx(1.5f),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(r, r),
            end = Offset(r + r * 0.4f, r),
            strokeWidth = dpToPx(1.5f),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun CalendarIcon(color: Color) {
    Canvas(modifier = Modifier.size(14.dp)) {
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = color,
            topLeft = Offset(0f, dpToPx(2f)),
            size = Size(w, h - dpToPx(2f)),
            cornerRadius = CornerRadius(dpToPx(2f), dpToPx(2f)),
            style = Stroke(width = dpToPx(1.5f))
        )

        drawLine(
            color = color,
            start = Offset(0f, dpToPx(5f)),
            end = Offset(w, dpToPx(5f)),
            strokeWidth = dpToPx(1.5f)
        )

        // Draw small binder connectors
        drawLine(
            color = color,
            start = Offset(w * 0.25f, 0f),
            end = Offset(w * 0.25f, dpToPx(3f)),
            strokeWidth = dpToPx(1.5f),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.75f, 0f),
            end = Offset(w * 0.75f, dpToPx(3f)),
            strokeWidth = dpToPx(1.5f),
            cap = StrokeCap.Round
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.dpToPx(dp: Float): Float {
    return dp * density
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.dpToPx(dp: Int): Float {
    return dp.toFloat() * density
}
