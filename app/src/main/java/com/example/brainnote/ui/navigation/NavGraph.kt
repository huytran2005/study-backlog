package com.example.brainnote.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.brainnote.feature.auth.login.LoginScreen
import com.example.brainnote.feature.auth.forgotpassword.CreatePasswordScreen
import com.example.brainnote.feature.auth.forgotpassword.VerificationCodeScreen
import com.example.brainnote.feature.auth.forgotpassword.ForgotPasswordScreen
import com.example.brainnote.feature.auth.register.RegisterScreen
import com.example.brainnote.feature.onboarding.OnboardingScreen
import com.example.brainnote.feature.onboarding.OnboardingOption
import com.example.brainnote.feature.splash.SplashScreen
import com.example.brainnote.feature.home.HomeScreen
import com.example.brainnote.feature.home.AddNoteScreen

/**
 * Route constants for the application navigation graph.
 */
object BrainNoteDestinations {
    const val SPLASH_ROUTE = "splash"
    const val ONBOARDING_ROUTE = "onboarding"
    const val LOGIN_ROUTE = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFICATION_CODE = "verification_code"
    const val CREATE_PASSWORD = "create_password"
    const val REGISTER = "register"
    const val HOME_ROUTE = "home"
    const val ADD_NOTE_ROUTE = "add_note"
}

/**
 * StudyBacklogApp hosts the Navigation Host managing state transitions
 * between Splash, Onboarding, and Login Screens.
 */
@Composable
fun StudyBacklogApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
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
                    if (option == OnboardingOption.WITHOUT_LOGIN) {
                        navController.navigate(BrainNoteDestinations.HOME_ROUTE) {
                            popUpTo(BrainNoteDestinations.ONBOARDING_ROUTE) { inclusive = true }
                        }
                    } else {
                        navController.navigate(BrainNoteDestinations.LOGIN_ROUTE)
                    }
                }
            )
        }

        // Login Screen Destination
        composable(BrainNoteDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(BrainNoteDestinations.HOME_ROUTE) {
                        popUpTo(BrainNoteDestinations.ONBOARDING_ROUTE) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(BrainNoteDestinations.REGISTER)
                },
                onForgotPasswordClick = {
                    navController.navigate(BrainNoteDestinations.FORGOT_PASSWORD)
                }
            )
        }

        // Home Screen Destination
        composable(BrainNoteDestinations.HOME_ROUTE) {
            HomeScreen(
                onAddNoteClick = {
                    navController.navigate(BrainNoteDestinations.ADD_NOTE_ROUTE)
                }
            )
        }

        // Add Note Destination
        composable(BrainNoteDestinations.ADD_NOTE_ROUTE) {
            AddNoteScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { title, content, color ->
                    Toast.makeText(context, "Note Saved!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }

        // Forgot Password Screen Destination
        composable(BrainNoteDestinations.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onSuccess = {
                    navController.navigate(BrainNoteDestinations.VERIFICATION_CODE)
                }
            )
        }

        // Verification Code Screen Destination
        composable(BrainNoteDestinations.VERIFICATION_CODE) {
            VerificationCodeScreen(
                onBackClick = {
                    navController.popBackStack(BrainNoteDestinations.LOGIN_ROUTE, inclusive = false)
                },
                onCodeVerified = {
                    navController.navigate(BrainNoteDestinations.CREATE_PASSWORD)
                }
            )
        }

        // Create Password Screen Destination
        composable(BrainNoteDestinations.CREATE_PASSWORD) {
            CreatePasswordScreen(
                onBackClick = {
                    navController.popBackStack(BrainNoteDestinations.LOGIN_ROUTE, inclusive = false)
                },
                onPasswordCreated = {
                    Toast.makeText(context, "Password created successfully!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack(BrainNoteDestinations.LOGIN_ROUTE, inclusive = false)
                }
            )
        }

        // Register Screen Destination
        composable(BrainNoteDestinations.REGISTER) {
            RegisterScreen(
                navController = navController
            )
        }
    }
}
