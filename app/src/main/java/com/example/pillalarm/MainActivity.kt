package com.example.pillalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pillalarm.ui.screen.FirebaseAuthRepository
import com.example.pillalarm.auth.LoginViewModel
import com.example.pillalarm.ui.screen.LoginScreen
import com.example.pillalarm.ui.screen.SignupScreen
import com.example.pillalarm.ui.screen.SplashScreen
import com.example.pillalarm.ui.screen.HomeScreen
import com.example.pillalarm.ui.theme.PillAlarmTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = FirebaseAuthRepository()
        val factory = LoginViewModel.Factory(repo)

        setContent {
            PillAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate("login") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
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
                    }
                }
            }
        }
    }
}