package com.example.brainnote.feature.splash

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.brainnote.R

/**
 * SplashScreen renders the centered animated GIF from resources on a solid purple background (#6C43B8),
 * displaying the application title below in responsive and bold white typography, alongside a CTA button.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    uiState: SplashUiState = SplashUiState(),
    onGetStartedClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Setup Coil ImageLoader with GIF decoding capabilities (using ImageDecoder on API 28+)
    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF6C43B8)), // Purple background theme (#6C43B8)
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Animated onboarding/splash GIF
            AsyncImage(
                model = R.raw.splash_onboarding,
                imageLoader = imageLoader,
                contentDescription = "Splash Onboarding Animation",
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .padding(16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Styled App Title
            Text(
                text = uiState.appTitle,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primary CTA Button
            Button(
                onClick = onGetStartedClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp), // full width with horizontal padding
                shape = RoundedCornerShape(28.dp) // pill shape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Let's Get Started",
                        color = Color(0xFF6C43B8), // Brand Purple
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Arrow Forward",
                        tint = Color(0xFF6C43B8),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
