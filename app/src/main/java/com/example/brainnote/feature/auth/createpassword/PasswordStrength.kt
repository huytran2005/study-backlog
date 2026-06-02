package com.example.brainnote.feature.auth.createpassword

import androidx.compose.ui.graphics.Color

/**
 * PasswordStrength defines the security rating of a password.
 * Bundles label and color for immediate visual rendering.
 */
enum class PasswordStrength(val label: String, val color: Color) {
    WEAK("Weak", Color(0xFFE53935)),       // Red
    MEDIUM("Medium", Color(0xFFFBC02D)),   // Yellow/Amber
    STRONG("Strong", Color(0xFF388E3C))    // Green
}
