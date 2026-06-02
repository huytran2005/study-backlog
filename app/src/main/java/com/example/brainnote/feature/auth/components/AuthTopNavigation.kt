package com.example.brainnote.feature.auth.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A shared header navigation component for authentication screens (e.g. Register, Forgot Password).
 * Displays a back arrow and text to return to the Login screen.
 *
 * @param onBackClick Action to perform when the header is clicked.
 * @param modifier Custom modifier.
 * @param text The text displayed next to the arrow.
 */
@Composable
fun AuthTopNavigation(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Back to Login"
) {
    val brandPurple = Color(0xFF6C43B8)
    
    Row(
        modifier = modifier
            .clickable(
                onClick = onBackClick,
                onClickLabel = "Navigate back"
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Arrow pointing left",
            tint = brandPurple,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = brandPurple,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
