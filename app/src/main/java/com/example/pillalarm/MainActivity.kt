package com.example.pillalarm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pillalarm.auth.LoginViewModel
import com.example.pillalarm.ui.screen.DisclaimerDialog
import com.example.pillalarm.ui.screen.FirebaseAuthRepository
import com.example.pillalarm.ui.screen.HomeScreen
import com.example.pillalarm.ui.screen.LoginScreen
import com.example.pillalarm.ui.screen.MyMedicinesScreen
import com.example.pillalarm.ui.screen.SignupScreen
import com.example.pillalarm.ui.screen.SplashScreen
import com.example.pillalarm.ui.theme.PillAlarmTheme
import androidx.core.content.edit

class MainActivity : ComponentActivity() {

    // Permission launcher
    // This handles the user's choice (Allow vs Don't Allow) from the popup.
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted: The app can now post notifications.
        } else {
            // Permission denied: Inform the user that alarms won't work properly.
            Toast.makeText(
                this,
                "Notifications are needed for the Alarm to ring!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check permission for notifications
        askNotificationPermission()

        // View model for login
        val repo = FirebaseAuthRepository()
        val factory = LoginViewModel.Factory(repo)

        setContent {
            PillAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()


                    // 1. SETUP TERMS OF SERVICE CHECK
                    val context = LocalContext.current

                    // Access internal storage (SharedPreferences) to remember if user agreed
                    val sharedPreferences = remember {
                        context.getSharedPreferences("app_prefs", MODE_PRIVATE)
                    }

                    // Check the saved value (Default is false)
                    var hasAgreedToTerms by remember {
                        mutableStateOf(sharedPreferences.getBoolean("terms_accepted", false))
                    }

                    // 2. MAIN APP CONTENT (Background)
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen { destination ->
                                navController.navigate(destination) {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                        composable("login") {
                            val loginVm: LoginViewModel = viewModel(factory = factory)
                            LoginScreen(
                                onNavigateToSignUp = {
                                    navController.navigate("signup")
                                },
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                loginViewModel = loginVm
                            )
                        }
                        composable("signup") {
                            SignupScreen(
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        composable("my_medicines") {
                            MyMedicinesScreen(navController)
                        }
                    }

                    // 3. THE POPUP OVERLAY

                    // If the user has NOT agreed yet, show the dialog on top of everything.
                    if (!hasAgreedToTerms) {
                        DisclaimerDialog(
                            onAgreeClicked = {
                                // 1. Save "true" to storage so it never shows again
                                sharedPreferences.edit { putBoolean("terms_accepted", true) }
                                // 2. Update state to dismiss the dialog immediately
                                hasAgreedToTerms = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Notification permission logic
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}