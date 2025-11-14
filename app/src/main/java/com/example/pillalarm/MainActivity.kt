package com.example.pillalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.example.pillalarm.ui.screen.LoginSignupScreen
import com.example.pillalarm.ui.screen.SplashScreen
import com.example.pillalarm.ui.theme.PillAlarmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PillAlarmTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var showSplash by remember { mutableStateOf(true) }

                    if (showSplash) {
                        SplashScreen {
                            showSplash = false
                        }
                    } else {
                        LoginSignupScreen()
                    }
                }
            }
        }
    }
}
