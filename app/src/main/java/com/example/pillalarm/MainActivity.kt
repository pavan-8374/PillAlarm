package com.example.pillalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import com.example.pillalarm.ui.screen.SplashScreen
import com.example.pillalarm.ui.screen.LoginSignupScreen
import com.example.pillalarm.ui.theme.PillAlarmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PillAlarmTheme {
                var showSplash by remember { mutableStateOf(true) }

                Surface(color = MaterialTheme.colorScheme.background) {
                    if (showSplash) {
                        SplashScreen { showSplash = false }
                    } else {
                        LoginSignupScreen()
                    }
                }
            }
        }
    }
}
