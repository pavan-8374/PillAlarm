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
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pillalarm.auth.LoginViewModel
import com.example.pillalarm.ui.screen.FirebaseAuthRepository
import com.example.pillalarm.ui.screen.HomeScreen
import com.example.pillalarm.ui.screen.LoginScreen
import com.example.pillalarm.ui.screen.MyMedicinesScreen
import com.example.pillalarm.ui.screen.SignupScreen
import com.example.pillalarm.ui.screen.SplashScreen
import com.example.pillalarm.ui.theme.PillAlarmTheme

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
        // As soon as the app starts, we check if we need to ask for permission.
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
                    // Navigation process >> splash > login > signup > home
                    val navController = rememberNavController()

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
                        composable("login") { // login screen
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
                        composable("signup") { // signup screen
                            SignupScreen(
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("home") { // home screen
                            HomeScreen(navController = navController)
                        }
                        composable("my_medicines") {
                            MyMedicinesScreen(navController)
                        }
                    }
                }
            }
        }
    }
    // Notification permission logic
    // This logic checks the Android version and current permission status.
    private fun askNotificationPermission() {
        // This is only necessary for Android 13+ versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // If permission is already granted. Do nothing.
            } else {
                // Permission is missing. Launch the popup to ask the user.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}