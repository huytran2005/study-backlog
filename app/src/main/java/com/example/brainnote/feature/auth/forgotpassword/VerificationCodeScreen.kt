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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainnote.feature.auth.components.AuthButton
import com.example.brainnote.feature.auth.components.BrainNoteTextField

@Composable
fun VerificationCodeScreen(
    modifier: Modifier = Modifier,
    viewModel: VerificationCodeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onCodeVerified: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    VerificationCodeScreenContent(
        uiState = uiState,
        onCodeChange = { viewModel.onCodeChanged(it) },
        onContinueClick = { viewModel.submitCode(onSuccess = onCodeVerified) },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun VerificationCodeScreenContent(
    uiState: VerificationCodeUiState,
    onCodeChange: (String) -> Unit,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandPurple = Color(0xFF6C43B8)
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
            // 1. Back Navigation Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onBackClick)
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = brandPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back to Login",
                    color = brandPurple,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Content Section
            Text(
                text = "Verify Code",
                fontSize = 38.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Bold,
                color = textDarkColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Enter the verification code sent to your email",
                fontSize = 16.sp,
                color = Color(0xFF79747E),
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // 3. Form
            BrainNoteTextField(
                value = uiState.code,
                onValueChange = onCodeChange,
                label = "Verification Code",
                placeholder = "000000",
                errorText = uiState.codeError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Action Button
            AuthButton(
                text = "Continue",
                onClick = onContinueClick,
                isLoading = uiState.isLoading,
                enabled = uiState.isFormValid && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { testTag = "ContinueButton" }
            )
        }
    }
}

