package com.example.brainnote.feature.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.brainnote.R

@Composable
fun AnimatedGalaxyBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "tasks_bg")

    val cloud1X by infiniteTransition.animateFloat(
        initialValue = -400f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(120000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tasks_cloud1"
    )

    val cloud2X by infiniteTransition.animateFloat(
        initialValue = -600f,
        targetValue = 1800f,
        animationSpec = infiniteRepeatable(
            animation = tween(180000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tasks_cloud2"
    )

    val cloud3X by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 1600f,
        animationSpec = infiniteRepeatable(
            animation = tween(150000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tasks_cloud3"
    )

    val cloud4X by infiniteTransition.animateFloat(
        initialValue = -800f,
        targetValue = 1900f,
        animationSpec = infiniteRepeatable(
            animation = tween(200000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tasks_cloud4"
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Galaxy Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0B061A), // Deepest dark space purple
                            Color(0xFF1B113D), // Deep indigo
                            Color(0xFF382361), // Mid galaxy purple
                            Color(0xFF67408A), // Lighter purple horizon
                            Color(0xFF9E65A8)  // Pinkish gradient near the mountain
                        )
                    )
                )
        )

        // Floating Clouds
        Image(
            painter = painterResource(id = R.drawable.home_cloud),
            contentDescription = "Cloud 1",
            modifier = Modifier
                .offset(x = cloud1X.dp, y = 150.dp)
                .size(280.dp)
                .alpha(0.5f),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.home_cloud),
            contentDescription = "Cloud 2",
            modifier = Modifier
                .offset(x = cloud2X.dp, y = 350.dp)
                .size(140.dp)
                .alpha(0.35f),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.home_cloud),
            contentDescription = "Cloud 3",
            modifier = Modifier
                .offset(x = cloud3X.dp, y = 600.dp)
                .size(350.dp)
                .alpha(0.2f),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.home_cloud),
            contentDescription = "Cloud 4",
            modifier = Modifier
                .offset(x = cloud4X.dp, y = 50.dp)
                .size(100.dp)
                .alpha(0.6f),
            contentScale = ContentScale.Fit
        )

        // Mountain image at the bottom
        Image(
            painter = painterResource(id = R.drawable.home_mou),
            contentDescription = "Mountain background",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = 130.dp)
                .alpha(0.25f),
            contentScale = ContentScale.FillWidth
        )
    }
}
