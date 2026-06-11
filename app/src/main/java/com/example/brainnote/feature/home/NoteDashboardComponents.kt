package com.example.brainnote.feature.home

import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.brainnote.R

// Note Dashboard Color Palette
val NotePurple = Color(0xFF7445C8)
val NotePurpleLight = Color(0xFFEEE8F8)
val NoteYellow = Color(0xFFF6E6A5)
val NoteBeige = Color(0xFFECE9C9)

val NoteGrayText = Color(0xFF757575)
val NoteDarkText = Color(0xFF1E1E1E)

/**
 * Base note card component with elevation shadow, rounded corners (20.dp), and hover/press translateY animations.
 */
@Composable
fun BaseNoteCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.(Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val isActive = (isHovered || isPressed) && onClick != null

    // Smooth hover/tap translation
    val translationY by animateDpAsState(
        targetValue = if (isActive) (-6).dp else 0.dp,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
    )

    // Animated shadow elevation
    val elevationShadow by animateDpAsState(
        targetValue = if (isActive) 12.dp else 4.dp,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .offset(y = translationY)
            .shadow(
                elevation = elevationShadow,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .border(1.dp, Color.Black.copy(alpha = 0.03f), RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
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
        content(isHovered && onClick != null)
    }
}

/**
 * Large Hero banner with gradient background, clean titles, and a floating character illustration.
 */
@Composable
fun HeroCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    // Floating infinite animation for illustration
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 28.dp, bottomEnd = 28.dp),
                spotColor = NotePurple.copy(alpha = 0.2f)
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF7445C8), Color(0xFF6A3FBF)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                ),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 28.dp, bottomEnd = 28.dp))
            .padding(start = 28.dp, top = 48.dp, bottom = 24.dp, end = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Content
            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            // Right Illustration (occupies 45% width, using home_illustration1 PNG)
            Image(
                painter = painterResource(id = R.drawable.home_illustration1),
                contentDescription = "Hero Illustration",
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .offset(y = floatOffset.dp)
            )
        }
    }
}

/**
 * Card Type 1: IdeaCard with lightbulb, title (28sp), description, and footer strip.
 */
@Composable
fun IdeaCard(
    title: String,
    description: String,
    footerText: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    BaseNoteCard(modifier = modifier, backgroundColor = Color.White, onClick = onClick) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Lightbulb emoji/icon
                Text(text = "💡", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = title,
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = NoteDarkText
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = NoteGrayText,
                    lineHeight = 18.sp,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Footer strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA))
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Text(
                    text = footerText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NoteGrayText
                )
            }
        }
    }
}

/**
 * Card Type 2: ImageIdeaCard with pastel background, custom abstract graphics, and purple footer button.
 */
@Composable
fun ImageIdeaCard(
    title: String,
    description: String,
    footerText: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    BaseNoteCard(modifier = modifier, backgroundColor = NotePurpleLight, onClick = onClick) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NoteDarkText
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Abstract graphic instead of raw image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                ) {
                    AbstractGraphicDrawing(modifier = Modifier.fillMaxSize())
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = NoteGrayText,
                    lineHeight = 18.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Purple footer button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(NotePurple)
                    .clickable { }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = footerText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Card Type 3: ShoppingListCard with checkable items and highlight bar.
 */
@Composable
fun ShoppingListCard(
    title: String,
    items: List<String>,
    footerText: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    BaseNoteCard(modifier = modifier, backgroundColor = NoteYellow, onClick = onClick) {
        var completedStates by remember { mutableStateOf(items.map { false }) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NoteDarkText
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Checklist
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEachIndexed { index, itemText ->
                        val isChecked = completedStates[index]
                        NoteShoppingItemRow(
                            itemText = itemText,
                            isChecked = isChecked,
                            onCheckedChange = {
                                val newList = completedStates.toMutableList()
                                newList[index] = !isChecked
                                completedStates = newList
                            }
                        )
                    }
                }
            }

            // Yellow highlight bar at bottom
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFFEAB308))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = footerText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NoteDarkText.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun NoteShoppingItemRow(
    itemText: String,
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCheckedChange)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .border(
                    width = 1.5.dp,
                    color = if (isChecked) NotePurple else Color(0xFF6B7280).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(4.dp)
                )
                .background(
                    color = if (isChecked) NotePurple else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "checked",
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = itemText,
            fontSize = 13.sp,
            color = if (isChecked) NoteGrayText else NoteDarkText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Card Type 4: NestedTaskCard representing shopping lists with indented subtasks.
 */
@Composable
fun NestedTaskCard(
    title: String,
    tasks: List<Pair<String, List<String>>>,
    footerText: String,
    modifier: Modifier = Modifier,
    description: String = "",
    onClick: (() -> Unit)? = null
) {
    BaseNoteCard(modifier = modifier, backgroundColor = NoteBeige, onClick = onClick) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NoteDarkText
                )

                if (description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = NoteGrayText,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Nested Lists
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    tasks.forEach { task ->
                        Column {
                            // Primary Item
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .border(1.2.dp, NoteDarkText.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = task.first,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NoteDarkText
                                )
                            }

                            // Sub Items
                            Column(
                                modifier = Modifier.padding(start = 22.dp, top = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                task.second.forEach { subItem ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .border(1.dp, NoteDarkText.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = subItem,
                                            fontSize = 12.sp,
                                            color = NoteGrayText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom highlight bar
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color(0xFF84CC16).copy(alpha = 0.7f))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = footerText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NoteDarkText.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Reusable animated FloatingActionButton with scale-on-press gesture feedback.
 */
@Composable
fun FloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()


    Box(
        modifier = modifier
            .offset(y = 20.dp) // naturally float overlap
            .size(72.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                spotColor = NotePurple.copy(alpha = 0.4f),
                ambientColor = NotePurple.copy(alpha = 0.15f)
            )
            .background(NotePurple, CircleShape)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            // Plus vector drawing
            Canvas(modifier = Modifier.fillMaxSize()) {
                val len = size.width
                val mid = len / 2
                val thick = 3.dp.toPx()
                drawLine(
                    color = Color.White,
                    start = Offset(mid, 0f),
                    end = Offset(mid, len),
                    strokeWidth = thick,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White,
                    start = Offset(0f, mid),
                    end = Offset(len, mid),
                    strokeWidth = thick,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * Vector drawing for a modern flat character sitting with floating stars and books.
 */
@Composable
fun FlatPersonIllustration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Clean Canvas Drawing for premium illustration
        val cx = w * 0.5f
        val cy = h * 0.5f

        // Draw soft back glow circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                center = Offset(cx, cy),
                radius = w * 0.45f
            ),
            radius = w * 0.45f,
            center = Offset(cx, cy)
        )

        // Draw body sitting block
        val headR = w * 0.12f
        val headX = cx
        val headY = cy - h * 0.18f

        // 1. Head
        drawCircle(
            color = Color(0xFFFFE4E6), // Pastel rose skin
            radius = headR,
            center = Offset(headX, headY)
        )

        // Hair block
        val hairPath = Path().apply {
            moveTo(headX - headR, headY)
            quadraticTo(headX, headY - headR * 1.5f, headX + headR, headY)
            quadraticTo(headX + headR * 0.3f, headY - headR * 0.2f, headX - headR, headY)
        }
        drawPath(path = hairPath, color = Color(0xFF1E1E2F))

        // 2. Sweater / Torso (Rounded Trapezoid)
        val torsoPath = Path().apply {
            moveTo(cx - w * 0.16f, cy - h * 0.05f)
            quadraticTo(cx, cy - h * 0.15f, cx + w * 0.16f, cy - h * 0.05f)
            lineTo(cx + w * 0.22f, cy + h * 0.2f)
            lineTo(cx - w * 0.22f, cy + h * 0.2f)
            close()
        }
        drawPath(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFDA4AF), Color(0xFFF43F5E))
            ),
            path = torsoPath
        )

        // 3. Floating Books / Stars
        // Yellow Star 1
        drawStar(cx - w * 0.38f, cy - h * 0.25f, w * 0.04f)
        // Yellow Star 2
        drawStar(cx + w * 0.38f, cy + h * 0.15f, w * 0.05f)

        // Floating Notebook (diagonal rect)
        val bookW = w * 0.22f
        val bookH = h * 0.16f
        val bookX = cx - w * 0.35f
        val bookY = cy + h * 0.05f
        drawRoundRect(
            color = Color(0xFF38BDF8),
            topLeft = Offset(bookX, bookY),
            size = Size(bookW, bookH),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        // Book inner lines
        drawLine(
            color = Color.White.copy(alpha = 0.7f),
            start = Offset(bookX + bookW * 0.2f, bookY + bookH * 0.3f),
            end = Offset(bookX + bookW * 0.8f, bookY + bookH * 0.3f),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.7f),
            start = Offset(bookX + bookW * 0.2f, bookY + bookH * 0.6f),
            end = Offset(bookX + bookW * 0.8f, bookY + bookH * 0.6f),
            strokeWidth = 2.dp.toPx()
        )
    }
}

/**
 * Draws a clean star shape on a Canvas
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(x: Float, y: Float, radius: Float) {
    val path = Path().apply {
        moveTo(x, y - radius)
        lineTo(x + radius * 0.3f, y - radius * 0.3f)
        lineTo(x + radius, y)
        lineTo(x + radius * 0.3f, y + radius * 0.3f)
        lineTo(x, y + radius)
        lineTo(x - radius * 0.3f, y + radius * 0.3f)
        lineTo(x - radius, y)
        lineTo(x - radius * 0.3f, y - radius * 0.3f)
        close()
    }
    drawPath(color = Color(0xFFFDE047), path = path)
}

/**
 * Vector drawing for an abstract layout graphic inside the ImageIdeaCard.
 */
@Composable
fun AbstractGraphicDrawing(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Draw background grid pattern
        drawRect(color = Color(0xFFFAF9F6))

        // Draw soft purple circles
        drawCircle(
            color = NotePurpleLight.copy(alpha = 0.5f),
            radius = w * 0.25f,
            center = Offset(w * 0.2f, h * 0.5f)
        )
        drawCircle(
            color = Color(0xFFD8B4FE).copy(alpha = 0.4f),
            radius = w * 0.15f,
            center = Offset(w * 0.75f, h * 0.3f)
        )

        // Draw curved bezier line art representing a layout wave
        val wavePath = Path().apply {
            moveTo(0f, h * 0.7f)
            quadraticTo(w * 0.25f, h * 0.3f, w * 0.5f, h * 0.6f)
            quadraticTo(w * 0.75f, h * 0.9f, w, h * 0.4f)
        }
        drawPath(
            path = wavePath,
            color = NotePurple,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw small layout dots
        drawCircle(color = Color(0xFFF43F5E), radius = 4.dp.toPx(), center = Offset(w * 0.5f, h * 0.6f))
        drawCircle(color = Color(0xFF22C55E), radius = 4.dp.toPx(), center = Offset(w * 0.25f, h * 0.45f))
    }
}
