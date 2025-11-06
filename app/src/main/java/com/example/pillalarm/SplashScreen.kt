package com.example.pillalarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SplashPreview() {
    com.example.pillalarm.ui.theme.PillAlarmTheme {
        SplashScreen(onTimeout = {})
    }
}
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Run side-effect when the composable first appears
    LaunchedEffect(Unit) {
        delay(9000) // ⏳ Wait 9 seconds
        onTimeout() // Then call the callback to move forward
    }

    // UI of splash screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pill Alarm",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
