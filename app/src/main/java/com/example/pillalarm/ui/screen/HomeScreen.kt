package com.example.pillalarm.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.pillalarm.ui.theme.PillAlarmTheme

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to PillAlarm Home!",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PillAlarmTheme {
        HomeScreen()
    }
}