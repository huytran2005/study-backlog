package com.example.brainnote.feature.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.alpha
import com.example.brainnote.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Custom Shape that draws a card with a premium wavy sloped top edge (as seen in weather screenshots).
 */
class WavyCardShape(private val cornerRadiusDp: Float = 24f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            val r = cornerRadiusDp * density.density

            // Start at top-left corner
            moveTo(0f, r)
            quadraticTo(0f, 0f, r, 0f)

            // Draw curved/sloped top line to top-right (wavy slope down from left to right)
            cubicTo(
                w * 0.35f, -h * 0.02f,
                w * 0.65f, h * 0.12f,
                w - r, h * 0.08f
            )
            quadraticTo(w, h * 0.08f, w, h * 0.08f + r)

            // Line to bottom-right
            lineTo(w, h - r)
            quadraticTo(w, h, w - r, h)

            // Line to bottom-left
            lineTo(r, h)
            quadraticTo(0f, h, 0f, h - r)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun getBentoColors(): BentoTheme {
    // Force dark colors because the background of NoteDashboard and HomeScreen is always the dark starry night sky.
    return BentoTheme(
        background = Color(0xFF0F0B18),       // Deep twilight slate/purple
        cardBg = Color(0xFF9E78B2).copy(alpha = 0.24f), // Translucent premium glassmorphic lilac
        cardBorder = Color(0xFFEADBFC).copy(alpha = 0.20f), // Soft glowing border
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFE5D5F2),
        accentPurple = Color(0xFFD6A5FA),      // Electric Lavender
        accentGreen = Color(0xFF8CEFA2),
        accentGold = Color(0xFFFCD34D),
        gradientStart = Color(0xFF8A60A8).copy(alpha = 0.85f), // Soft brand gradient
        gradientEnd = Color(0xFFC795C6).copy(alpha = 0.85f),
        badgeBg = Color(0xFF4C3070).copy(alpha = 0.5f)
    )
}

data class BentoTheme(
    val background: Color,
    val cardBg: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentPurple: Color,
    val accentGreen: Color,
    val accentGold: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val badgeBg: Color
)

@Composable
fun BentoCell(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundBrush: Brush? = null,
    backgroundColor: Color? = null,
    shape: Shape = RoundedCornerShape(24.dp),
    content: @Composable BoxScope.(BentoTheme, Boolean) -> Unit
) {
    val colors = getBentoColors()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isActive = (isHovered || isPressed) && onClick != null

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isActive) 12.dp else 2.dp,
                shape = shape,
                ambientColor = colors.textPrimary.copy(alpha = 0.02f),
                spotColor = colors.accentPurple.copy(alpha = 0.08f)
            )
            .then(
                if (backgroundBrush != null) {
                    Modifier.background(backgroundBrush, shape)
                } else {
                    Modifier.background(backgroundColor ?: colors.cardBg, shape)
                }
            )
            .border(1.dp, colors.cardBorder, shape)
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier
                        .hoverable(interactionSource)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                } else Modifier
            )
    ) {
        content(colors, isHovered)
    }
}

/**
 * Shared action arrow badge used in various Bento Cells
 */
@Composable
fun ActionArrowBadge(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    iconColor: Color,
    badgeSize: androidx.compose.ui.unit.Dp = 24.dp,
    iconSize: androidx.compose.ui.unit.Dp = 9.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 1.8.dp
) {
    Box(
        modifier = modifier
            .size(badgeSize)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(iconSize)) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w * 0.1f, h * 0.9f)
                lineTo(w * 0.9f, h * 0.1f)
                moveTo(w * 0.4f, h * 0.1f)
                lineTo(w * 0.9f, h * 0.1f)
                lineTo(w * 0.9f, h * 0.6f)
            }
            drawPath(
                path = path,
                color = iconColor,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Premium Hero Bento Cell with gradient background and circular action icon at top-right
 */
@Composable
fun HeroBentoCell(
    todayFocusTime: String,
    modifier: Modifier = Modifier
) {
    val colors = getBentoColors()
    val gradientBrush = Brush.linearGradient(
        colors = listOf(colors.gradientStart, colors.gradientEnd),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    BentoCell(
        modifier = modifier,
        backgroundBrush = gradientBrush,
        shape = WavyCardShape(24f)
    ) { _, _ ->
        // Organic split background wave (exactly matching the user reference screenshot style)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.42f, 0f)
                cubicTo(
                    w * 0.58f, h * 0.25f,
                    w * 0.32f, h * 0.75f,
                    w * 0.52f, h
                )
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = path,
                color = Color.Black.copy(alpha = 0.12f)
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Circular action arrow badge at top-right (replicated from screen)
            // Circular action arrow badge at top-right (replicated from screen)
            ActionArrowBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                backgroundColor = Color.White.copy(alpha = 0.2f),
                iconColor = Color.White,
                badgeSize = 32.dp,
                iconSize = 12.dp,
                strokeWidth = 2.dp
            )

            // 3D Illustration overlapping/jutting out of the card (replicated from weather screen)
            Image(
                painter = painterResource(id = R.drawable.home_iilustration),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(105.dp)
                    .offset(x = 10.dp, y = 10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "THỜI GIAN TẬP TRUNG",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                
                Column {
                    Text(
                        text = todayFocusTime,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-1.2).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Học sâu hôm nay",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Clean Streak Bento Cell with circular action arrow badge
 */
@Composable
fun StreakBentoCell(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    BentoCell(
        modifier = modifier,
        shape = WavyCardShape(24f)
    ) { colors, _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Clean action badge (replicated from screen)
            // Clean action badge (replicated from screen)
            ActionArrowBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                backgroundColor = colors.badgeBg,
                iconColor = colors.accentPurple
            )

            // 3D Illustration overlapping the streak card
            Image(
                painter = painterResource(id = R.drawable.home_illustration1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(64.dp)
                    .offset(x = 10.dp, y = 10.dp)
                    .alpha(0.85f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CHUỖI HỌC",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                    letterSpacing = 0.5.sp
                )
                Column {
                    Text(
                        text = "$streakDays Ngày",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.accentGold
                    )
                    Text(
                        text = "Liên tục",
                        fontSize = 10.sp,
                        color = colors.textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Clean Stats Bento Cell with circular action arrow badge
 */
@Composable
fun StatsBentoCell(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    BentoCell(
        modifier = modifier,
        shape = WavyCardShape(24f)
    ) { colors, _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            ActionArrowBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                backgroundColor = colors.badgeBg,
                iconColor = colors.accentPurple
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label.uppercase(),
                    fontSize = 8.5.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.textPrimary
                )
            }
        }
    }
}

/**
 * Start Session Bento Action Cell with Gradient Highlight
 */
@Composable
fun ActionBentoCell(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = getBentoColors()
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(colors.gradientStart, colors.gradientEnd)
    )

    BentoCell(
        modifier = modifier,
        onClick = onClick,
        backgroundBrush = gradientBrush
    ) { _, _ ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
            )
        }
    }
}

/**
 * Weekly progress cell with clean radial indicator (resembling the screen's productivity gauges)
 */
@Composable
fun WeeklyProgressBentoCell(
    currentHours: Float,
    targetHours: Float,
    modifier: Modifier = Modifier
) {
    val progressFraction = (currentHours / targetHours).coerceIn(0f, 1f)

    BentoCell(modifier = modifier) { colors, _ ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "MỤC TIÊU TUẦN NÀY",
                    fontSize = 8.5.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${currentHours}h đã tích lũy",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Mục tiêu tuần: ${targetHours}h",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Gauge progress ring (as seen in screenshot)
            Box(
                modifier = Modifier.size(52.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeW = 4.dp.toPx()
                    drawCircle(
                        color = colors.cardBorder,
                        radius = (size.width - strokeW) / 2f,
                        style = Stroke(width = strokeW)
                    )
                    drawArc(
                        color = colors.accentPurple,
                        startAngle = -90f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${(progressFraction * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentPurple
                )
            }
        }
    }
}

/**
 * Minimalist clean badge row for Bento grid listing
 */
@Composable
fun AchievementBentoCell(
    title: String,
    description: String,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = getBentoColors()
    val titleColor = if (isUnlocked) colors.textPrimary else colors.textPrimary.copy(alpha = 0.4f)
    val badgeBg = if (isUnlocked) colors.accentPurple.copy(alpha = 0.15f) else colors.cardBorder
    val badgeText = if (isUnlocked) "Đạt" else "Khóa"
    val badgeColor = if (isUnlocked) colors.accentPurple else colors.textSecondary

    BentoCell(modifier = modifier) { _, _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                // Small minimalist text pill
                Box(
                    modifier = Modifier
                        .background(
                            badgeBg,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }
            Text(
                text = description,
                fontSize = 10.5.sp,
                color = colors.textSecondary,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Minimalist cell for simple notes without achievement badges
 */
@Composable
fun NoteBentoCell(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val colors = getBentoColors()

    BentoCell(modifier = modifier) { _, _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = description,
                fontSize = 10.5.sp,
                color = colors.textSecondary,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Re-implemented ShoppingListCard matching new bento gradient theme
 */
@Composable
fun ShoppingListCard(
    title: String,
    items: List<String>,
    footerText: String,
    modifier: Modifier = Modifier
) {
    val colors = getBentoColors()
    BentoCell(modifier = modifier) { _, _ ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(colors.accentPurple, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = item, fontSize = 12.sp, color = colors.textSecondary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = footerText.uppercase(),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accentPurple,
                letterSpacing = 0.5.sp
            )
        }
    }
}
