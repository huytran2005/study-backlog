package com.example.brainnote.feature.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainnote.feature.auth.components.AuthButton
import com.example.brainnote.feature.auth.components.AuthTextField
import com.example.brainnote.feature.auth.components.GoogleLoginButton
import com.example.brainnote.ui.theme.BrainNoteTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Stateful container for the Login Screen.
 * Manages form state, performs validation, hoists callbacks, and handles loading simulation.
 *
 * @param modifier Screen modifiers.
 * @param onLoginSuccess Callback triggered on successful authentication.
 * @param onRegisterClick Callback when the register hyperlink is tapped.
 * @param onForgotPasswordClick Callback when the forgot password link is tapped.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    // Hoisted form state
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val uiState = LoginUiState(
        email = email,
        password = password,
        isLoading = isLoading,
        emailError = emailError,
        passwordError = passwordError
    )

    // Form validation helper
    fun validateAndSubmit() {
        focusManager.clearFocus()
        var isValid = true

        // 1. Email validation
        if (email.isBlank()) {
            emailError = "Email cannot be empty."
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Please enter a valid email address."
            isValid = false
        } else {
            emailError = null
        }

        // 2. Password validation
        if (password.isBlank()) {
            passwordError = "Password cannot be empty."
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters."
            isValid = false
        } else {
            passwordError = null
        }

        if (isValid) {
            // Simulate API authentication call
            coroutineScope.launch {
                isLoading = true
                delay(1500) // 1.5 seconds loading simulation
                isLoading = false
                onLoginSuccess()
            }
        }
    }

    LoginScreenContent(
        modifier = modifier,
        uiState = uiState,
        onEmailChange = {
            email = it
            if (emailError != null) emailError = null // Clear error dynamically
        },
        onPasswordChange = {
            password = it
            if (passwordError != null) passwordError = null // Clear error dynamically
        },
        onLoginClick = { validateAndSubmit() },
        onGoogleLoginClick = {
            // Simulate Google SSO call
            coroutineScope.launch {
                isLoading = true
                delay(1000)
                isLoading = false
                onLoginSuccess()
            }
        },
        onForgotPasswordClick = onForgotPasswordClick,
        onRegisterClick = onRegisterClick
    )
}

/**
 * Stateless UI rendering component for the Login Screen.
 * Built with full accessibility in mind, supporting responsive design on small screens/landscape.
 */
@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandPurple = Color(0xFF6C43B8)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding() // Absolute edge-to-edge support
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Responsive scrolling
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Large Title
            Text(
                text = "Let's Login",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = brandPurple,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "And notes your idea",
                fontSize = 16.sp,
                color = Color(0xFF79747E),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Email text field
            AuthTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Email",
                leadingIcon = Icons.Default.Email,
                error = uiState.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password text field
            AuthTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                error = uiState.passwordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Forgot Password Link
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Forgot Password?",
                    color = brandPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable(onClick = onForgotPasswordClick)
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Login Button
            AuthButton(
                text = "Login",
                onClick = onLoginClick,
                isLoading = uiState.isLoading,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Custom "OR" Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
                Text(
                    text = "OR",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google SSO Login Button
            GoogleLoginButton(
                onClick = onGoogleLoginClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(48.dp))

            // Register Footer
            val footerText = buildAnnotatedString {
                append("Don't have any account? ")
                withStyle(
                    style = SpanStyle(
                        color = brandPurple,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Register here")
                }
            }

            Text(
                text = footerText,
                fontSize = 14.sp,
                color = Color(0xFF49454F),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable(onClick = onRegisterClick)
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty Form Login Screen")
@Composable
fun LoginScreenEmptyPreview() {
    BrainNoteTheme {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onGoogleLoginClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Valid Form Login Screen")
@Composable
fun LoginScreenFilledPreview() {
    BrainNoteTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "user@brainnote.com",
                password = "mypassword"
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onGoogleLoginClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {}
        )
    }
}
