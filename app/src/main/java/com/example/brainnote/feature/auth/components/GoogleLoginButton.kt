package com.example.brainnote.feature.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainnote.R
import com.example.brainnote.ui.theme.BrainNoteTheme

/**
 * An outlined, premium social login button for authenticating via Google.
 * Uses the custom multi-colored Google logo.
 *
 * @param onClick Event callback when clicked.
 * @param modifier Layout modifiers.
 * @param enabled Active status.
 */
@Composable
fun GoogleLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(28.dp), // Premium pill-shape
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1C1B1F) // Neutral dark text
        ),
        modifier = modifier.height(56.dp) // Large touch target
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Official Multi-color Google 'G' Vector Icon
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                tint = Color.Unspecified, // Retains official Google colors
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Sign in with Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.25.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoogleLoginButtonPreview() {
    BrainNoteTheme {
        GoogleLoginButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
