package com.example.brainnote.feature.auth.register

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.brainnote.feature.auth.components.AuthButton
import com.example.brainnote.feature.auth.components.AuthScreenContainer
import com.example.brainnote.feature.auth.components.AuthTopNavigation
import com.example.brainnote.feature.auth.components.BrainNoteTextField
import com.example.brainnote.ui.theme.BrainNoteTheme

/**
 * Stateful container for the Register Screen.
 * Obtains the RegisterViewModel, observes the UI State flow,
 * and delegates UI presentation.
 *
 * @param navController Navigation Controller to pop the stack or navigate further.
 * @param modifier Screen modifiers.
 * @param viewModel State management ViewModel.
 */
@Composable
fun RegisterScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    RegisterScreenContent(
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChanged,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onRegisterClick = {
            viewModel.registerUser {
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Go back to login screen on successful registration
            }
        },
        onBackClick = {
            navController.popBackStack()
        },
        modifier = modifier
    )
}

/**
 * Stateless UI rendering component for the Register Screen.
 * Fully responsive, keyboard-avoiding, and pixel-perfect.
 */
@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandPurple = Color(0xFF6C43B8)
    val textDarkColor = Color(0xFF1D1633)
    val subtitleColor = Color(0xFF79747E)
    
    val focusManager = LocalFocusManager.current

    AuthScreenContainer(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        innerPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
            
            // 1. Top Navigation
            AuthTopNavigation(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(36.dp))

            // 2. Content Header Section
            Text(
                text = "Let's Register",
                fontSize = 36.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Bold,
                color = brandPurple,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create an account to start noting your ideas",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = subtitleColor,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Register Form
            // Name Field
            BrainNoteTextField(
                value = uiState.fullName,
                onValueChange = onFullNameChange,
                label = "Full Name",
                placeholder = "Your Name",
                errorText = uiState.fullNameError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().testTag("Full NameField")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email Field
            BrainNoteTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "test@example.com",
                errorText = uiState.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().testTag("EmailField")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            BrainNoteTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "********",
                isPassword = true,
                errorText = uiState.passwordError,
                helperText = "At least 8 characters, including uppercase, lowercase and number",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (uiState.isFormValid) onRegisterClick()
                    }
                ),
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().testTag("PasswordField")
            )

            // Dynamic layout pushes the button to the bottom when screen size allows
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(48.dp))

            // 4. Primary CTA button
            AuthButton(
                text = "Register",
                onClick = {
                    focusManager.clearFocus()
                    onRegisterClick()
                },
                enabled = uiState.isFormValid,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth().testTag("RegisterButton")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Footer Link "Already have an account? Login here"
            val footerText = buildAnnotatedString {
                append("Already have an account? ")
                withStyle(
                    style = SpanStyle(
                        color = brandPurple,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Login here")
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = footerText,
                    fontSize = 14.sp,
                    color = Color(0xFF49454F),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable(
                            onClick = onBackClick,
                            onClickLabel = "Navigate back to login screen"
                        )
                        .padding(12.dp)
                )
            }
        }
}

// --- Preview Support ---

@Preview(showBackground = true, name = "Empty Register Screen")
@Composable
fun RegisterScreenEmptyPreview() {
    BrainNoteTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(),
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRegisterClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Filled Register Screen")
@Composable
fun RegisterScreenFilledPreview() {
    BrainNoteTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(
                fullName = "Alex Mercer",
                email = "alex@mercer.com",
                password = "Password123"
            ),
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRegisterClick = {},
            onBackClick = {}
        )
    }
}
