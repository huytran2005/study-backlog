package com.example.brainnote.feature.auth.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A shared password visibility toggle icon button to reduce code duplication and cognitive complexity.
 */
@Composable
fun PasswordVisibilityToggle(
    passwordVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit
) {
    val image = if (passwordVisible) {
        Icons.Filled.Visibility
    } else {
        Icons.Filled.VisibilityOff
    }

    val description = if (passwordVisible) "Hide password" else "Show password"

    IconButton(onClick = { onVisibilityChange(!passwordVisible) }) {
        Icon(
            imageVector = image,
            contentDescription = description,
            tint = Color(0xFF79747E)
        )
    }
}

/**
 * Shared helper for visual transformation of passwords.
 */
fun getPasswordVisualTransformation(isPassword: Boolean, passwordVisible: Boolean): VisualTransformation {
    return if (isPassword && !passwordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }
}
