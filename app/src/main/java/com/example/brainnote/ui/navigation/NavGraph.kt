package com.example.brainnote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brainnote.feature.onboarding.OnboardingScreen
import com.example.brainnote.feature.splash.SplashScreen

/**
 * Route constants for the application navigation graph.
 */
object BrainNoteDestinations {
    const val SPLASH_ROUTE = "splash"
    const val ONBOARDING_ROUTE = "onboarding"
}

/**
 * StudyBacklogApp hosts the Navigation Host managing state transitions
 * between Splash and Onboarding Screens.
 */
@Composable
fun StudyBacklogApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = BrainNoteDestinations.SPLASH_ROUTE
    ) {
        // Splash Screen Destination
        composable(BrainNoteDestinations.SPLASH_ROUTE) {
            SplashScreen(
                onGetStartedClick = {
                    navController.navigate(BrainNoteDestinations.ONBOARDING_ROUTE) {
                        // Pop splash screen off the backstack so pressing back from onboarding exits
                        popUpTo(BrainNoteDestinations.SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding Screen Destination
        composable(BrainNoteDestinations.ONBOARDING_ROUTE) {
            OnboardingScreen(
                onContinueClick = { option ->
                    // Log selection and handle transition when onboarding is completed
                    println("Onboarding completed with option: $option")
                }
            )
        }
    }
}
