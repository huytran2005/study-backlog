package com.example.brainnote.feature.auth.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brainnote.feature.auth.components.AuthButton
import com.example.brainnote.feature.auth.components.BrainNoteTextField
import com.example.brainnote.ui.theme.BrainNoteTheme

/**
 * Stateful container for the Create Password Screen.
 * Retrieves/creates the ViewModel, collects the state as a Compose State flow,
 * and manages navigation callbacks.
 *
 * @param onBackClick Triggers when back navigation is tapped.
 * @param onPasswordCreated Triggers after the password is successfully updated.
 * @param modifier Screen layout modifier.
 * @param viewModel Hosted business logic ViewModel.
 */
@Composable
fun CreatePasswordScreen(
    onBackClick: () -> Unit,
    onPasswordCreated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatePasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CreatePasswordScreenContent(
        uiState = uiState,
        onPasswordChange = viewModel::onPasswordChanged,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChanged,
        onSubmit = {
            viewModel.createPassword(onPasswordCreated)
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

/**
 * Stateless UI rendering component for the Create Password Screen.
 * Optimized for accessibility, responsiveness (scrollable column), and pixel-perfect design.
 */
@Composable
fun CreatePasswordScreenContent(
    uiState: CreatePasswordUiState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Design Tokens
    val brandPurple = Color(0xFF6C43B8)
    val titleColor = Color(0xFF1D1633)
    val subtitleColor = Color(0xFF79747E)
    
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding() // Safe area support (statusBar, navigationBar padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // Keyboard-avoiding behavior
                .verticalScroll(rememberScrollState()) // Support landscape and smaller screens
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            
            // 1. Top Navigation
            Row(
                modifier = Modifier
                    .clickable(
                        onClick = onBackClick,
                        onClickLabel = "Back to login screen"
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
                    text = "Back to Login",
                    color = brandPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 2. Content Section (Heading & Subtitle)
            Text(
                text = "Create a New\nPassword",
                fontSize = 38.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your new password should be different from the previous password",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = subtitleColor,
                modifier = Modifier.fillMaxWidth(0.9f) // Slight limit for premium multi-line balance
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Password Form
            // Field 1: New Password
            BrainNoteTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "New Password",
                placeholder = "********",
                isPassword = true,
                errorText = uiState.passwordError,
                helperText = "min. 8 character, combination of 0-9, A-Z, a-z",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Live Password Strength Indicator (only render when password has some input)
            if (uiState.password.isNotEmpty()) {
                PasswordStrengthIndicator(
                    strength = uiState.strength,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Field 2: Retype New Password
            BrainNoteTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Retype New Password",
                placeholder = "********",
                isPassword = true,
                errorText = uiState.confirmPasswordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (uiState.isFormValid) onSubmit()
                    }
                ),
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Bottom Action CTA Button
            AuthButton(
                text = "Create Password",
                onClick = {
                    focusManager.clearFocus()
                    onSubmit()
                },
                enabled = uiState.isFormValid,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// --- Preview Support ---

@Preview(showBackground = true, name = "1. Empty State")
@Composable
fun EmptyStatePreview() {
    BrainNoteTheme {
        CreatePasswordScreenContent(
            uiState = CreatePasswordUiState(),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onSubmit = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Invalid Password State")
@Composable
fun InvalidPasswordPreview() {
    BrainNoteTheme {
        CreatePasswordScreenContent(
            uiState = CreatePasswordUiState(
                password = "simple",
                passwordError = "At least 8 characters required.",
                strength = PasswordStrength.WEAK
            ),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onSubmit = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "3. Password Mismatch State")
@Composable
fun PasswordMismatchPreview() {
    BrainNoteTheme {
        CreatePasswordScreenContent(
            uiState = CreatePasswordUiState(
                password = "Password123",
                confirmPassword = "Password321",
                confirmPasswordError = "Passwords must match",
                strength = PasswordStrength.MEDIUM
            ),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onSubmit = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "4. Valid State")
@Composable
fun ValidStatePreview() {
    BrainNoteTheme {
        CreatePasswordScreenContent(
            uiState = CreatePasswordUiState(
                password = "SecurePassword1!",
                confirmPassword = "SecurePassword1!",
                strength = PasswordStrength.STRONG
            ),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onSubmit = {},
            onBackClick = {}
        )
    }
}

