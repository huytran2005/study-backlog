package com.example.brainnote.feature.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.brainnote.ui.theme.BrainNoteTheme

/**
 * A highly styled, pill-shaped brand action button for Authentication flows.
 * Handles loading spinner integration natively.
 *
 * @param text Title text inside the button.
 * @param onClick Event callback when button is clicked.
 * @param modifier Layout modifiers.
 * @param enabled Active status.
 * @param isLoading Triggers showing a circular loading spinner inside the button.
 */
@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val brandPurple = Color(0xFF6C43B8)

    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = brandPurple,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFE0E0E0),
            disabledContentColor = Color(0xFF9E9E9E)
        ),
        modifier = modifier.height(56.dp).testTag("${text}Button"), // Premium comfortable touch targets
        shape = RoundedCornerShape(28.dp)  // Pill-shaped rounded styling
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Active State Button")
@Composable
fun AuthButtonActivePreview() {
    BrainNoteTheme {
        AuthButton(
            text = "Login",
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Loading State Button")
@Composable
fun AuthButtonLoadingPreview() {
    BrainNoteTheme {
        AuthButton(
            text = "Login",
            onClick = {},
            isLoading = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
