package com.example.brainnote.feature.onboarding

/**
 * OnboardingOption represents the available usage paths for the user.
 */
enum class OnboardingOption {
    SYNC_AND_SAVE,
    WITHOUT_LOGIN
}

/**
 * OnboardingUiState holds the UI state representing visual titles, subtitles, and selection state.
 */
data class OnboardingUiState(
    val title: String = "Choose how you want to use BrainNote",
    val subtitle: String = "Select the experience that fits your workflow.",
    val selectedOption: OnboardingOption? = null
) {
    val isContinueEnabled: Boolean
        get() = selectedOption != null
}
