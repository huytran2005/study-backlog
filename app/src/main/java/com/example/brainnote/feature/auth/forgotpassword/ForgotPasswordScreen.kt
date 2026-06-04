package com.example.brainnote.feature.auth.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainnote.feature.auth.components.AuthButton
import com.example.brainnote.feature.auth.components.BrainNoteTextField

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    ForgotPasswordScreenContent(
        uiState = uiState,
        onEmailChange = { viewModel.onEmailChanged(it) },
        onSubmitClick = { viewModel.submitEmail(onSuccess = onSuccess) },
        modifier = modifier
    )
}

@Composable
fun ForgotPasswordScreenContent(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textDarkColor = Color(0xFF1D1633)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Forgot Password",
                fontSize = 38.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Bold,
                color = textDarkColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Enter your email to receive a verification code",
                fontSize = 16.sp,
                color = Color(0xFF79747E),
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            BrainNoteTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Email Address",
                placeholder = "your@email.com",
                errorText = uiState.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthButton(
                text = "Send Code",
                onClick = onSubmitClick,
                isLoading = uiState.isLoading,
                enabled = uiState.isFormValid && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

