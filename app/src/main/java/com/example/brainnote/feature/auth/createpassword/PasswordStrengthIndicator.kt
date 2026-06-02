package com.example.brainnote.feature.auth.createpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainnote.ui.theme.BrainNoteTheme

/**
 * A beautiful, modern visual indicator for password strength.
 * Renders 3 clean rounded horizontal segments and a dynamic security text label.
 *
 * @param strength The currently calculated PasswordStrength.
 * @param modifier Modifier for external positioning constraints.
 */
@Composable
fun PasswordStrengthIndicator(
    strength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    val activeSegments = when (strength) {
        PasswordStrength.WEAK -> 1
        PasswordStrength.MEDIUM -> 2
        PasswordStrength.STRONG -> 3
    }
    val color = strength.color

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 3-Segment rounded bar system
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (i in 0 until 3) {
                val segmentColor = if (i < activeSegments) color else Color(0xFFECEFF1)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(segmentColor, RoundedCornerShape(999.dp)) // Pill-shaped segments
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Dynamic text status label
        Text(
            text = strength.label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.width(60.dp) // Fixed width for alignment stability
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeakIndicatorPreview() {
    BrainNoteTheme {
        PasswordStrengthIndicator(
            strength = PasswordStrength.WEAK,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MediumIndicatorPreview() {
    BrainNoteTheme {
        PasswordStrengthIndicator(
            strength = PasswordStrength.MEDIUM,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StrongIndicatorPreview() {
    BrainNoteTheme {
        PasswordStrengthIndicator(
            strength = PasswordStrength.STRONG,
            modifier = Modifier.padding(16.dp)
        )
    }
}
