package com.example.pillalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
// Importing the SplashScreen and loginScreen.kt file from the ui.screen package
import com.example.pillalarm.ui.screen.SplashScreen
import com.example.pillalarm.ui.screen.LoginScreen
import com.example.pillalarm.ui.screen.SignupScreen
import com.example.pillalarm.ui.theme.PillAlarmTheme

class LoginSignupScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PillAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Define 'showSplash'
                    var showSplash by remember { mutableStateOf(true) }

                    // This state now controls which one login/signup screen to visible
                    var isLoginMode by remember { mutableStateOf(true) }

                    if (showSplash) {
                        // This line calls all the code from your SplashScreen.Skt file
                        SplashScreen(
                            onSplashFinished = {
                                // When splash is done, set this to false
                                showSplash = false
                            }
                        )
                    }
                    else if (isLoginMode) {
                        // Show LoginScreen
                        LoginScreen(
                            onNavigateToSignUp = {
                                // When user clicks Sign Up, set mode to false
                                isLoginMode = false
                            }
                        )
                    } else {
                        // Show SignupScreen
                        SignupScreen(
                            onNavigateToLogin = {
                                // When user clicks Login, set mode to true
                                isLoginMode = true
                            }
                        )
                    }
                }
            }
        }
    }
}