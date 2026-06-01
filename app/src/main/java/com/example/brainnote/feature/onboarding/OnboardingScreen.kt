package com.example.brainnote.feature.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brainnote.R
import com.example.brainnote.ui.theme.BrainNoteTheme

/**
 * Stateful entry point for the Onboarding Screen.
 * Hoists the selection state using rememberSaveable to survive configuration changes.
 *
 * @param modifier The modifier to be applied to the screen layout.
 * @param onContinueClick Callback invoked when the user selects an option and clicks Continue.
 */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onContinueClick: (OnboardingOption) -> Unit = {}
) {
    // Hoist selected option state so it is persistent across configuration changes (e.g. rotation)
    var selectedOption by rememberSaveable { mutableStateOf<OnboardingOption?>(null) }

    val uiState = OnboardingUiState(
        selectedOption = selectedOption
    )

    OnboardingScreenContent(
        modifier = modifier,
        uiState = uiState,
        onOptionSelected = { option ->
            selectedOption = option
        },
        onContinueClick = {
            selectedOption?.let { onContinueClick(it) }
        }
    )
}

/**
 * Stateless UI rendering component for the Onboarding Screen.
 * Fully previewable and independent of state management.
 *
 * @param uiState Holds current screen titles, subtitles, and selection state.
 * @param onOptionSelected Callback invoked when a card is tapped.
 * @param onContinueClick Callback invoked when the continue button is tapped.
 * @param modifier The modifier to be applied to the screen layout.
 */
@Composable
fun OnboardingScreenContent(
    uiState: OnboardingUiState,
    onOptionSelected: (OnboardingOption) -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White) // Clean white background as required
            .safeDrawingPadding() // Ensures proper inset spacing under status/navigation bars
    ) {
        // Vertical scroll enabled to ensure responsiveness on small screens/landscape orientation
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top illustration from R.drawable.chosse_onboarding (Enlarged based on user request)
            Image(
                painter = painterResource(id = R.drawable.chosse_onboarding),
                contentDescription = "Onboarding Illustration",
                modifier = Modifier
                    .fillMaxWidth(0.95f) // Wider for prominent visual
                    .height(300.dp) // Explicitly set height larger (enlarged)
                    .padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Large Title
            Text(
                text = uiState.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1B1F), // Dark text matching Material 3 OnBackground
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = uiState.subtitle,
                fontSize = 15.sp,
                color = Color(0xFF49454F), // Medium dark text matching Material 3 Variant
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Selectable Card Option A with Cloud Sync icon
            SelectionCard(
                title = "Sync & Save on Server",
                description = "Secure cloud synchronization across devices.",
                selected = uiState.selectedOption == OnboardingOption.SYNC_AND_SAVE,
                icon = Icons.Default.Cloud,
                onClick = { onOptionSelected(OnboardingOption.SYNC_AND_SAVE) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selectable Card Option B with Smartphone/Local icon
            SelectionCard(
                title = "Use Without Login",
                description = "Store data only on this device.",
                selected = uiState.selectedOption == OnboardingOption.WITHOUT_LOGIN,
                icon = Icons.Default.Smartphone,
                onClick = { onOptionSelected(OnboardingOption.WITHOUT_LOGIN) },
                modifier = Modifier.fillMaxWidth()
            )

            // Dynamic bottom spacing ensuring content pushes above bottom button
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            )

            // Bottom Continue Button
            ContinueButton(
                enabled = uiState.isContinueEnabled,
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

/**
 * Reusable, custom styled, and fully accessible selection card representing an option.
 *
 * @param title Bold header text for the option.
 * @param description Detailed text describing the choice.
 * @param selected Boolean indicating if this card is currently selected.
 * @param icon The leading vector icon to display in front of the option.
 * @param onClick Callback when the card is tapped.
 * @param modifier The modifier to be applied to the card.
 */
@Composable
fun SelectionCard(
    title: String,
    description: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dynamic brand colors (Accent purple #6A3EA1)
    val purpleAccent = Color(0xFF6A3EA1)
    val borderStrokeColor = if (selected) purpleAccent else Color(0xFFE0E0E0)
    val cardBackgroundColor = if (selected) Color(0x0A6A3EA1) else Color.White // Subtle 4% opacity tint when active
    val borderThickness = if (selected) 2.dp else 1.dp

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp), // Premium rounded cards (20dp+)
        border = BorderStroke(borderThickness, borderStrokeColor),
        color = cardBackgroundColor,
        modifier = modifier
            .semantics(mergeDescendants = true) {
                // Combines texts and states so TalkBack reads it as a single cohesive element
                this.selected = selected
                this.role = Role.RadioButton
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon (Sync/Device)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) purpleAccent else Color(0xFF79747E),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) purpleAccent else Color(0xFF1C1B1F)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF49454F),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Standard RadioButton to clearly indicate selection behavior
            RadioButton(
                selected = selected,
                onClick = null, // Handled by parent Surface click
                colors = RadioButtonDefaults.colors(
                    selectedColor = purpleAccent,
                    unselectedColor = Color(0xFF79747E)
                )
            )
        }
    }
}

/**
 * Reusable, pill-shaped Action Button aligned with the theme style.
 *
 * @param enabled Whether the button is active and clickable.
 * @param onClick Callback when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 */
@Composable
fun ContinueButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val purpleAccent = Color(0xFF6A3EA1)

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = purpleAccent,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFE0E0E0),
            disabledContentColor = Color(0xFF9E9E9E)
        ),
        modifier = modifier.height(56.dp), // Comfortable tap-target size
        shape = RoundedCornerShape(28.dp) // pill shape
    ) {
        Text(
            text = "Continue",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Preview(showBackground = true, name = "Unselected Onboarding Screen")
@Composable
fun OnboardingScreenUnselectedPreview() {
    BrainNoteTheme {
        OnboardingScreenContent(
            uiState = OnboardingUiState(selectedOption = null),
            onOptionSelected = {},
            onContinueClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Selected Onboarding Screen")
@Composable
fun OnboardingScreenSelectedPreview() {
    BrainNoteTheme {
        OnboardingScreenContent(
            uiState = OnboardingUiState(selectedOption = OnboardingOption.SYNC_AND_SAVE),
            onOptionSelected = {},
            onContinueClick = {}
        )
    }
}
