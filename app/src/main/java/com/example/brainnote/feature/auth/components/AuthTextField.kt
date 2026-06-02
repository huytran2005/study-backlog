package com.example.brainnote.feature.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainnote.ui.theme.BrainNoteTheme

/**
 * A highly reusable, beautiful, and accessible Outlined Text Field designed specifically for Authentication forms.
 *
 * @param value The text value to display in the field.
 * @param onValueChange Callback invoked when the text changes.
 * @param label Label text for the field.
 * @param leadingIcon Optional leading icon vector (e.g. Email, Lock).
 * @param modifier Modifier to be applied to the field.
 * @param isPassword Boolean indicating if this is a secret password field.
 * @param error Error message to show underneath if validation fails.
 * @param keyboardOptions Semantic keyboard settings.
 * @param keyboardActions Event actions for keyboard IME events.
 */
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector?,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    error: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    // Brand Purple Color (#6C43B8)
    val brandPurple = Color(0xFF6C43B8)
    val isError = error != null
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = label) },
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(16.dp), // Premium rounded styling matching BrainNote
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            brandPurple.copy(alpha = 0.8f)
                        }
                    )
                }
            },
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
                focusedLabelColor = brandPurple,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                unfocusedLabelColor = Color(0xFF79747E),
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error,
                cursorColor = brandPurple
            )
        )

        // Error message text displayed below with standard typography
        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Normal Text Field")
@Composable
fun AuthTextFieldNormalPreview() {
    BrainNoteTheme {
        AuthTextField(
            value = "test@example.com",
            onValueChange = {},
            label = "Email Address",
            leadingIcon = Icons.Default.Email,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Password Text Field with Error")
@Composable
fun AuthTextFieldPasswordPreview() {
    BrainNoteTheme {
        AuthTextField(
            value = "12345",
            onValueChange = {},
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            error = "Password must be at least 6 characters.",
            modifier = Modifier.padding(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
    }
}
