package com.example.brainnote.feature.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

enum class FocusState {
    FOCUSING,
    BREAKING
}

@Composable
fun FocusScreen(
    onCloseClick: () -> Unit = {},
    viewModel: FocusViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val focusState = uiState.focusState
    val isRunning = uiState.isRunning
    val timeRemaining = uiState.timeRemaining
    val showSettingsDialog = uiState.showSettingsDialog
    val focusDurationMinutes = uiState.focusDurationMinutes
    val breakDurationMinutes = uiState.breakDurationMinutes
    
    val focusDuration = focusDurationMinutes * 60
    val breakDuration = breakDurationMinutes * 60
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (focusState == FocusState.FOCUSING) {
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0B0B1F), Color(0xFF09091C))
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF27D17F), Color(0xFF06684A))
                    )
                }
            )
    ) {
        // Floating particles (reduced by 60%, total 6 particles, with different opacities and slow animation)
        AnimatedFloatingParticles(focusState = focusState)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with Settings on the right
            FocusHeader(
                title = if (focusState == FocusState.FOCUSING) "Focus Mode" else "Break Time",
                onCloseClick = onCloseClick,
                onSettingsClick = { viewModel.setShowSettingsDialog(true) }
            )

            // Center Countdown hero element
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val maxDuration = if (focusState == FocusState.FOCUSING) focusDuration else breakDuration
                val progress = if (maxDuration > 0) timeRemaining.toFloat() / maxDuration.toFloat() else 0f
                
                TimerRing(
                    progress = progress,
                    timeText = formatTime(timeRemaining),
                    state = focusState,
                    isRunning = isRunning
                )
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Control actions
                ControlActionSection(
                    focusState = focusState,
                    isRunning = isRunning,
                    onPlayPauseToggle = { viewModel.togglePlayPause() },
                    onSkipClick = { viewModel.skipSession() },
                    onResetClick = { viewModel.resetTimer() }
                )

                Spacer(modifier = Modifier.height(48.dp).navigationBarsPadding())
            }
        }
    }

    // Modal settings dialog
    if (showSettingsDialog) {
        FocusSettingsDialog(
            currentFocusMinutes = focusDurationMinutes,
            currentBreakMinutes = breakDurationMinutes,
            onDismiss = { viewModel.setShowSettingsDialog(false) },
            onSave = { newFocus, newBreak ->
                viewModel.updateSettings(newFocus, newBreak)
            }
        )
    }
}

@Composable
fun FocusHeader(
    title: String,
    onCloseClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Close button: glass background, 1px white border, blur 12dp
        Box(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterStart)
                .blur(12.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                .clickable { onCloseClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        // Title text centered
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Center)
        )

        // Settings button: glass background, 1px white border, blur 12dp
        Box(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .blur(12.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                .clickable { onSettingsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun TimerRing(
    progress: Float,
    timeText: String,
    state: FocusState,
    isRunning: Boolean
) {
    val accentColor = if (state == FocusState.FOCUSING) Color(0xFF8B5CF6) else Color(0xFF4ADE80)
    val glowColor = accentColor.copy(alpha = 0.35f)
    
    // Smooth progress animation
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "progressAnimation"
    )

    // Breathing pulse scale animation when running
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRunning) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .size(240.dp)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Soft outer purple/green radial glow behind the timer
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent)
                    )
                )
                .blur(32.dp)
        )

        Canvas(modifier = Modifier.size(220.dp)) {
            val strokeWidthPx = 10.dp.toPx()
            
            // Background ring track
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                style = Stroke(width = strokeWidthPx)
            )

            // Active arc using purple/green gradient
            val arcBrush = if (state == FocusState.FOCUSING) {
                Brush.sweepGradient(
                    colors = listOf(Color(0xFF8B5CF6), Color(0xFFC084FC), Color(0xFF8B5CF6))
                )
            } else {
                Brush.sweepGradient(
                    colors = listOf(Color(0xFF4ADE80), Color(0xFF22C55E), Color(0xFF4ADE80))
                )
            }

            drawArc(
                brush = arcBrush,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // Indicator dot with glow
            val angleRad = Math.toRadians((360f * animatedProgress - 90f).toDouble())
            val radius = size.width / 2
            val indicatorX = center.x + radius * cos(angleRad).toFloat()
            val indicatorY = center.y + radius * sin(angleRad).toFloat()

            // Outer indicator dot glow
            drawCircle(
                color = accentColor.copy(alpha = 0.6f),
                radius = 12.dp.toPx(),
                center = Offset(indicatorX, indicatorY)
            )

            // Inner solid dot
            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = Offset(indicatorX, indicatorY)
            )
        }

        // Inner Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🍃",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Text(
                text = timeText,
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (state == FocusState.FOCUSING) "Focus Time" else "Break Time",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MotivationCard() {
    var isFavorite by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Purple Icon Container
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎯", fontSize = 18.sp)
                }

                Column {
                    Text(
                        text = "Stay focused",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "You're doing great!",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            IconButton(onClick = { isFavorite = !isFavorite }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFE11D48) else Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun BreakInstructionCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Great work! 🎉",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Take a breather, stretch, and hydrate.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ControlActionSection(
    focusState: FocusState,
    isRunning: Boolean,
    onPlayPauseToggle: () -> Unit,
    onSkipClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset Button (56dp)
        ControlButton(
            onClick = onResetClick,
            size = 56.dp
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset timer",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Center primary control: Play button & End session directly below it
        val primaryColor = if (focusState == FocusState.FOCUSING) Color(0xFF8B5CF6) else Color.White
        val iconColor = if (focusState == FocusState.FOCUSING) Color.White else Color(0xFF06684A)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ControlButton(
                onClick = onPlayPauseToggle,
                size = 72.dp,
                isPrimary = true
            ) {
                if (isRunning) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp, 24.dp).background(iconColor, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.size(6.dp, 24.dp).background(iconColor, RoundedCornerShape(2.dp)))
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = iconColor,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (focusState == FocusState.FOCUSING) "End session" else "Skip break",
                color = Color.White.copy(alpha = 0.7f), // 70% opacity
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSkipClick() }
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Next / Skip Button (56dp)
        ControlButton(
            onClick = onSkipClick,
            size = 56.dp
        ) {
            Text(
                text = if (focusState == FocusState.BREAKING) "Skip" else "Next",
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ControlButton(
    onClick: () -> Unit,
    size: Dp,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "clickScale"
    )

    val backgroundModifier = if (isPrimary) {
        Modifier
            .size(size)
            .scale(scale)
            .shadow(24.dp, CircleShape, spotColor = Color(0xFF8B5CF6).copy(alpha = 0.45f))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFA78BFA), Color(0xFF7C3AED))
                ),
                CircleShape
            )
    } else {
        Modifier
            .size(size)
            .scale(scale)
            .background(Color.White.copy(alpha = 0.04f), CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
    }

    Box(
        modifier = modifier
            .then(backgroundModifier)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun AnimatedFloatingParticles(focusState: FocusState) {
    // Reduced to 6 particles, animated opacities and positions
    val particleCount = 6
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    val particleColor = if (focusState == FocusState.FOCUSING) Color(0xFF8B5CF6) else Color.White
    
    // Slow hovering animation paths using standard sine calculations
    val hoverOffsets = List(particleCount) { index ->
        val duration = (3000 + index * 800)
        val delayTime = index * 200
        val hoverValue by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, delayMillis = delayTime, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "hoverValue$index"
        )
        hoverValue
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        
        // Static seed coordinates
        val coordinates = listOf(
            Offset(0.15f, 0.25f),
            Offset(0.85f, 0.18f),
            Offset(0.25f, 0.75f),
            Offset(0.78f, 0.70f),
            Offset(0.50f, 0.12f),
            Offset(0.10f, 0.55f)
        )

        for (i in 0 until particleCount) {
            val baseCoord = coordinates[i]
            val animatedOffset = Offset(
                x = baseCoord.x * w + hoverOffsets[i] * sin(i.toDouble()).toFloat(),
                y = baseCoord.y * h + hoverOffsets[i] * cos(i.toDouble()).toFloat()
            )
            val opacity = 0.12f + (i * 0.05f) // variable opacity per particle
            
            drawCircle(
                color = particleColor.copy(alpha = opacity),
                radius = (3 + (i % 3)).dp.toPx(),
                center = animatedOffset
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusSettingsDialog(
    currentFocusMinutes: Int,
    currentBreakMinutes: Int,
    onDismiss: () -> Unit,
    onSave: (focusMin: Int, breakMin: Int) -> Unit
) {
    var focusMin by remember { mutableStateOf(currentFocusMinutes) }
    var breakMin by remember { mutableStateOf(currentBreakMinutes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onSave(focusMin, breakMin) },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF8B5CF6))
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.6f))
            ) {
                Text("Cancel")
            }
        },
        title = {
            Text(
                text = "Timer Settings",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Focus duration slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Focus Duration", color = Color.White, fontSize = 14.sp)
                        Text("$focusMin min", color = Color(0xFF8B5CF6), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = focusMin.toFloat(),
                        onValueChange = { focusMin = it.toInt() },
                        valueRange = 5f..60f,
                        steps = 11, // Steps of 5 min (5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60)
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF8B5CF6),
                            activeTrackColor = Color(0xFF8B5CF6),
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }

                // Break duration slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Break Duration", color = Color.White, fontSize = 14.sp)
                        Text("$breakMin min", color = Color(0xFF4ADE80), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = breakMin.toFloat(),
                        onValueChange = { breakMin = it.toInt() },
                        valueRange = 1f..30f,
                        steps = 29, // Steps of 1 min (1 to 30)
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF4ADE80),
                            activeTrackColor = Color(0xFF4ADE80),
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFF0F0F29),
        shape = RoundedCornerShape(24.dp)
    )
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
