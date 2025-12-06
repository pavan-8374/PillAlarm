package com.example.pillalarm.ui.screen


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

// Allows swapping between real Firebase and fake implementations.


interface AuthRepository {
    suspend fun signIn(email: String, password: String)
}

// Real implementation using FirebaseAuth.
class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth = Firebase.auth
) : AuthRepository {
    override suspend fun signIn(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw e
        }
    }
}

// Fake implementation for previews.

class FakeAuthRepository(
    private val succeed: Boolean = true
) : AuthRepository {
    override suspend fun signIn(email: String, password: String) {
        if (!succeed) {
            throw FirebaseAuthException("ERROR", "Fake login failed")
        }
    }
}