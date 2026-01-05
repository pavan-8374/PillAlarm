package com.example.pillalarm.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisclaimerDialog(
    onAgreeClicked: () -> Unit
) {
    // I set onDismissRequest to empty {} so the user cannot click outside to close it.
    // They must click the button.
    AlertDialog(
        onDismissRequest = { /* Do nothing, force user to agree */ },
        containerColor = Color.White,
        title = {
            Text(
                text = "Terms of Service & Disclaimer",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            // Make the text scrollable in case it's long on small screens
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Please read carefully before using this application:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 1. LIMITATION OF USING THIS APPLICATION
                Text(text = "1. Purpose of App", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "This application (Visual Pill Alarm) is designed for general aid and normal medication reminders only. It is not a certified medical application.",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 2. LIABILITY WAIVER
                Text(text = "2. Limitation of Liability", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "The developers are NOT responsible for any health consequences resulting from missed alarms, software bugs, battery failure, or device restrictions (e.g., Doze mode). Users should not rely solely on this app for critical life-saving medication.",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 3. LICENSING
                Text(text = "3. Open Source Licenses", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "This app utilizes open-source software under the Apache 2.0 and MIT Licenses, including:\n" +
                            "• Jetpack Compose\n• Firebase SDK\n• Coil Image Loader\n• Room Database",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAgreeClicked,
                modifier = Modifier.fillMaxWidth() // Make button wide
            ) {
                Text("I Agree and Continue")
            }
        },

        dismissButton = null // No "Cancel" button allowed
    )
}