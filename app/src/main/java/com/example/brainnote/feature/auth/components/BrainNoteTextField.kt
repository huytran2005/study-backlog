package com.example.brainnote.feature.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.brainnote.ui.theme.BrainNoteTheme

/**
 * A premium, highly customizable, and accessible reusable text input field
 * with the label positioned cleanly above the input field.
 *
 * @param value The text value to display in the field.
 * @param onValueChange Callback invoked when the text changes.
 * @param label The label text to show above the input field.
 * @param modifier Modifier to be applied to the column layout.
 * @param placeholder Optional placeholder text to show inside the input.
 * @param isPassword Boolean indicating if this is a secure password input field.
 * @param helperText Informational helper text shown below the field.
 * @param errorText Error message shown below the field (takes precedence over helper text).
 * @param keyboardOptions Keyboard options for custom IME actions and input types.
 * @param keyboardActions Event callbacks for keyboard actions.
 * @param enabled Controls the enabled state of the input.
 */
@Composable
fun BrainNoteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    helperText: String? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean = true
) {
    // Design Tokens
    val brandPurple = Color(0xFF6C43B8)
    val borderUnfocused = Color(0xFFDADADA)
    val textDarkColor = Color(0xFF1D1633)
    val isError = errorText != null
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Label styled above input
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = textDarkColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().testTag("${label}Field"),
            placeholder = if (placeholder.isNotEmpty()) {
                {
                    Text(
                        text = placeholder,
                        color = Color(0xFF9E9E9E),
                        fontSize = 15.sp
                    )
                }
            } else null,
            singleLine = true,
            isError = isError,
            enabled = enabled,
            shape = RoundedCornerShape(12.dp), // Radius 12.dp as per design spec
            trailingIcon = if (isPassword) {
                {
                    PasswordVisibilityToggle(
                        passwordVisible = passwordVisible,
                        onVisibilityChange = { passwordVisible = it }
                    )
                }
            } else null,
            visualTransformation = getPasswordVisualTransformation(isPassword, passwordVisible),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = brandPurple,
                unfocusedBorderColor = borderUnfocused,
                errorBorderColor = MaterialTheme.colorScheme.error,
                cursorColor = brandPurple,
                focusedTextColor = textDarkColor,
                unfocusedTextColor = textDarkColor,
                disabledBorderColor = Color(0xFFF1F1F1),
                disabledTextColor = Color(0xFF9E9E9E)
            )
        )

        // Helper or Error layout underneath
        if (errorText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        } else if (helperText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = helperText,
                color = Color(0xFF79747E),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BrainNoteTextFieldNormalPreview() {
    BrainNoteTheme {
        BrainNoteTextField(
            value = "",
            onValueChange = {},
            label = "New Password",
            placeholder = "********",
            isPassword = true,
            helperText = "At least 8 characters, including uppercase, lowercase and number",
            modifier = Modifier.padding(16.dp)
        )
    }
}
