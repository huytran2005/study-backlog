package com.example.brainnote.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brainnote.feature.splash.SplashScreen

/**
 * StudyBacklogApp hosts the Navigation Host managing state transitions
 * between Splash and Onboarding Screens.
 */
@Composable
fun StudyBacklogApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash Screen Destination
        composable("splash") {
            SplashScreen(
                onGetStartedClick = {
                    navController.navigate("onboarding")
                }
            )
        }
        
        // Onboarding Screen Placeholder Destination
        composable("onboarding") {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
