package com.example.pillalarm.auth

import com.example.pillalarm.ui.screen.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel that holds login state and handles authentication.
 * It depends on an AuthRepository to perform the authentication (Firebase).
 */
class LoginViewModel(private val repo: AuthRepository) : ViewModel() {

    // State flows observed by the UI
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Input handlers
    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }

    // Login action
    fun login(onSuccess: () -> Unit) {
        val e = email.value.trim()
        val p = password.value

        // Basic validation
        if (e.isBlank()) {
            _errorMessage.value = "Email required"
            return
        }
        if (p.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                repo.signIn(e, p)
                _isLoading.value = false
                onSuccess()
            } catch (ex: Exception) {
                _isLoading.value = false
                _errorMessage.value = ex.message ?: "Login failed"
            }
        }
    }

    // Factory for injecting repository
    class Factory(private val repo: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
