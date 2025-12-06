package com.example.pillalarm.ui.screen

// I am importing the necessary libraries which are essential for my splash screen implementation.
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image // Import the Image composable
import androidx.compose.ui.res.painterResource // Import the painter resource
import androidx.compose.ui.unit.dp
import com.example.pillalarm.R // To import my project file
import com.google.firebase.auth.FirebaseAuth


@Composable
fun SplashScreen(onSplashFinished: (String) -> Unit) {
    var visible by remember { mutableStateOf(true) }
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        delay(3000) // my splash screen Show for 3 seconds
        visible = false
        delay(200)
        if (user != null) {
            onSplashFinished("home")
        } else {
            onSplashFinished("login")
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(800))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Wrap in a Column to stack the logo and text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Adding Image composable
                Image(
                    painter = painterResource(id = R.drawable.pill_alarm_logo), // my app logo file name
                    contentDescription = "App Logo" // This is for accessibility
                )

                Spacer(modifier = Modifier.height(86.dp)) // This adds space between logo and below text

                // This shows my name in the app
                Text(
                    text = "Developed by Pavan Rallapalli",
                    fontSize = 20.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
// This shows the preview of the splash screen
    @Preview(showBackground = true)
    @Composable
    fun SplashScreenPreview() {
        MaterialTheme {
            SplashScreen(onSplashFinished = {})
        }
    }

