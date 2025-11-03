package com.example.pillalarm

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillalarm.ui.theme.PillAlarmTheme // Make sure this import matches your theme package

// Change this to ComponentActivity
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent replaces the old XML layout system
        setContent {
            // This 'PillAlarmTheme' is defined in your ui/theme/Theme.kt file
            // If it's named something else, change it here.
            PillAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This is our main Composable function that holds the UI
                    LoginSignupScreen()
                }
            }
        }
    }
}

/**
 * This Composable function builds the entire Login/Signup UI.
 */
@Composable
fun LoginSignupScreen() {
    // --- STATE MANAGEMENT ---
    // These 'remember' variables hold the state of our UI.
    // When they change, Compose automatically updates the UI.

    // Tracks if we are in "Login" (true) or "Sign Up" (false) mode
    var isLoginMode by remember { mutableStateOf(true) }

    // Stores the text for each input field
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Tracks password visibility
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Stores validation error messages
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Gets the current context, which is needed for showing Toasts
    val context = LocalContext.current

    // --- VALIDATION LOGIC ---
    fun validateInput(isLogin: Boolean): Boolean {
        // Clear all previous errors
        emailError = null
        passwordError = null
        confirmPasswordError = null

        var isValid = true

        // Email validation
        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Please enter a valid email"
            isValid = false
        }

        // Password validation
        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        }

        // "Confirm Password" validation (only for Sign Up)
        if (!isLogin) {
            if (confirmPassword.isBlank()) {
                confirmPasswordError = "Please confirm your password"
                isValid = false
            } else if (password != confirmPassword) {
                confirmPasswordError = "Passwords do not match"
                isValid = false
            }
        }

        return isValid
    }

    // --- ACTION HANDLERS ---
    fun handleLogin() {
        if (!validateInput(isLogin = true)) return

        // --- TODO: Add your actual Login logic here (e.g., Firebase Auth) ---
        Log.d("LoginSignupScreen", "Attempting login with Email: $email, Pass: $password")
        Toast.makeText(context, "Login Successful (simulation)", Toast.LENGTH_SHORT).show()
        // Here you would navigate to your app's main screen
    }

    fun handleSignup() {
        if (!validateInput(isLogin = false)) return

        // --- TODO: Add your actual Sign Up logic here (e.g., Firebase Auth) ---
        Log.d("LoginSignupScreen", "Attempting signup with Email: $email, Pass: $password")
        Toast.makeText(context, "Sign Up Successful (simulation)", Toast.LENGTH_SHORT).show()

        // Switch to login mode after successful signup
        isLoginMode = true
    }

    // --- UI LAYOUT (Composable) ---
    // Column arranges its children vertically
    Column(
        modifier = Modifier
            .fillMaxSize() // Take up the whole screen
            .padding(24.dp), // Add padding around the edges
        verticalArrangement = Arrangement.Center, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {

        // Title Text
        Text(
            text = if (isLoginMode) "Login" else "Sign Up",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp)) // A spacer for vertical distance

        // Email Input Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim(); emailError = null }, // Update state, clear error
            label = { Text("Enter your Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null, // Show error state if error is not null
            supportingText = { // This composable shows the error message
                if (emailError != null) {
                    Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            // This line hides the password characters
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            // This adds the little "eye" icon to toggle visibility
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "Confirm Password" field (Only shows in Sign Up mode)
        // We use a simple 'if' check to show/hide this
        if (!isLoginMode) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = confirmPasswordError != null,
                supportingText = {
                    if (confirmPasswordError != null) {
                        Text(text = confirmPasswordError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (confirmPasswordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Action Button (Login or Sign Up)
        Button(
            onClick = {
                if (isLoginMode) {
                    handleLogin()
                } else {
                    handleSignup()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoginMode) "Login" else "Sign Up", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Button (TextButton)
        TextButton(
            onClick = {
                isLoginMode = !isLoginMode // Flip the mode
                // Clear all errors and fields when toggling
                email = ""
                password = ""
                confirmPassword = ""
                emailError = null
                passwordError = null
                confirmPasswordError = null
            }
        ) {
            Text(
                text = if (isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Login",
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * This allows you to preview your UI in the "Split" or "Design" view
 * in Android Studio without having to run the app on an emulator.
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PillAlarmTheme {
        LoginSignupScreen()
    }
}

/**
 * A second preview to see what "Sign Up" mode looks like.
 */
@Preview(showBackground = true)
@Composable
fun SignupModePreview() {
    PillAlarmTheme {
        // We can't directly call LoginSignupScreen() and set isLoginMode.
        // So we'd have to refactor LoginSignupScreen to take `isLoginMode` as a parameter
        // to make it easily previewable in both states.
        // For now, this preview will just show the default "Login" state.
        // To see the "Sign Up" state, you'd run the app and tap the toggle button.
        LoginSignupScreen()
    }
}