package com.example.pillalarm.ui.screen

// I am importing the necessary libraries which are essential for my login screen implementation.
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.pillalarm.auth.LoginViewModel
import com.example.pillalarm.ui.theme.PillAlarmTheme

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel() // provided by Activity Preview
) {
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current

    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()
    val errorMsg by loginViewModel.errorMessage.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    // show error as Toast
    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            if (!isInPreview) Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }


    //  This is the UI for the login screen.
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", fontSize = 32.sp, style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { loginViewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (isInPreview) {
                    // preview behavior call success directly
                    onLoginSuccess()
                } else {
                    loginViewModel.login(onLoginSuccess)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onNavigateToSignUp, enabled = !isLoading) {
            Text("Don't have an account? Sign Up")
        }
    }
}

//This shows the preview of the login screen
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Using fake repo so preview doesn’t call Firebase
    val fakeRepo = FakeAuthRepository(succeed = true)
    val factory = LoginViewModel.Factory(fakeRepo)
    val vm: LoginViewModel = viewModel(factory = factory)

    PillAlarmTheme {
        LoginScreen(
            onNavigateToSignUp = {},
            onLoginSuccess = {},
            loginViewModel = vm
        )
    }
}