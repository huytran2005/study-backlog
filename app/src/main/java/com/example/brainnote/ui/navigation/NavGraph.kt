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
import com.example.brainnote.feature.home.CreateTaskScreen
import com.example.brainnote.feature.home.CreateWeeklyPlanScreen
import com.example.brainnote.feature.home.CreateGoalScreen
import com.example.brainnote.feature.home.NewNoteTypeScreen
import com.example.brainnote.feature.home.NoteRepository
import com.example.brainnote.feature.home.NoteCardData

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
    const val CREATE_TASK_ROUTE = "create_task"
    const val NEW_NOTE_TYPE_ROUTE = "new_note_type"
    const val CREATE_WEEKLY_PLAN_ROUTE = "create_weekly_plan"
    const val CREATE_GOAL_ROUTE = "create_goal"
}

/**
 * StudyBacklogApp hosts the Navigation Host managing state transitions
 * between Splash, Onboarding, and Login Screens.
 */
@Composable
fun StudyBacklogApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    NoteRepository.initialize(context)
    
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
                    navController.navigate(BrainNoteDestinations.NEW_NOTE_TYPE_ROUTE)
                },
                onTaskCardClick = { index ->
                    navController.navigate("${BrainNoteDestinations.CREATE_TASK_ROUTE}?taskIndex=$index")
                }
            )
        }

        // New Note Type Destination
        composable(BrainNoteDestinations.NEW_NOTE_TYPE_ROUTE) {
            NewNoteTypeScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onQuickNoteClick = {
                    navController.navigate(BrainNoteDestinations.ADD_NOTE_ROUTE) {
                        popUpTo(BrainNoteDestinations.NEW_NOTE_TYPE_ROUTE) { inclusive = true }
                    }
                },
                onWeeklyPlanClick = {
                    navController.navigate(BrainNoteDestinations.CREATE_WEEKLY_PLAN_ROUTE) {
                        popUpTo(BrainNoteDestinations.NEW_NOTE_TYPE_ROUTE) { inclusive = true }
                    }
                },
                onGoalClick = {
                    navController.navigate(BrainNoteDestinations.CREATE_GOAL_ROUTE) {
                        popUpTo(BrainNoteDestinations.NEW_NOTE_TYPE_ROUTE) { inclusive = true }
                    }
                },
                onTaskClick = {
                    navController.navigate(BrainNoteDestinations.CREATE_TASK_ROUTE) {
                        popUpTo(BrainNoteDestinations.NEW_NOTE_TYPE_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // Add Note Destination
        composable(BrainNoteDestinations.ADD_NOTE_ROUTE) {
            AddNoteScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { title, content, category ->
                    val newNote = NoteCardData.Idea(
                        title = title,
                        description = content,
                        footerText = "Category: $category"
                    )
                    NoteRepository.addNote(newNote)
                    Toast.makeText(context, "Note Saved!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }

        // Create Task Destination
        composable(
            route = "${BrainNoteDestinations.CREATE_TASK_ROUTE}?taskIndex={taskIndex}",
            arguments = listOf(
                androidx.navigation.navArgument("taskIndex") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val taskIndexStr = backStackEntry.arguments?.getString("taskIndex")
            val taskIndex = taskIndexStr?.toIntOrNull()
            
            CreateTaskScreen(
                taskIndex = taskIndex,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { title, description, dueDate, priority, category, checklist ->
                    val newTask = NoteCardData.NestedTask(
                        title = title,
                        description = description,
                        tasks = checklist,
                        footerText = "Priority: $priority | Due: $dueDate | Category: $category"
                    )
                    if (taskIndex != null) {
                        NoteRepository.updateNote(taskIndex, newTask)
                        Toast.makeText(context, "Task Updated Successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        NoteRepository.addNote(newTask)
                        Toast.makeText(context, "Task Saved Successfully!", Toast.LENGTH_SHORT).show()
                    }
                    navController.popBackStack()
                }
            )
        }

        // Create Weekly Plan Destination
        composable(BrainNoteDestinations.CREATE_WEEKLY_PLAN_ROUTE) {
            CreateWeeklyPlanScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { title, description, week, mainGoal, priority ->
                    val tasks = listOf(
                        Pair(description.ifBlank { "Plan Details" }, listOf("Goal: $mainGoal"))
                    )
                    val newPlan = NoteCardData.NestedTask(
                        title = "$title ($week)",
                        tasks = tasks,
                        footerText = "Priority: $priority"
                    )
                    NoteRepository.addNote(newPlan)
                    Toast.makeText(context, "Weekly Plan Saved!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }

        // Create Goal Destination
        composable(BrainNoteDestinations.CREATE_GOAL_ROUTE) {
            CreateGoalScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { title, description, startDate, endDate, status ->
                    val tasks = listOf(
                        Pair(description.ifBlank { "Goal Details" }, listOf("Start: $startDate", "End: $endDate"))
                    )
                    val newGoal = NoteCardData.NestedTask(
                        title = title,
                        tasks = tasks,
                        footerText = "Status: $status"
                    )
                    NoteRepository.addNote(newGoal)
                    Toast.makeText(context, "Goal Saved!", Toast.LENGTH_SHORT).show()
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
