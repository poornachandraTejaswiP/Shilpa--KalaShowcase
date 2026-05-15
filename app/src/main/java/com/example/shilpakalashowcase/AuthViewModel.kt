package com.example.shilpakalashowcase

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class UserRole {
    NONE, ARTISAN, CUSTOMER
}

class AuthViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isSignUpMode by mutableStateOf(false)
    var selectedRole by mutableStateOf(UserRole.NONE)
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun signInWithEmail(onSuccess: (UserRole) -> Unit) {
        if (!validateInputs()) return
        
        isLoading = true
        // Simulating success
        onSuccess(selectedRole)
        isLoading = false
    }

    fun signUpWithEmail(onSuccess: (UserRole) -> Unit) {
        if (!validateInputs()) return
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match"
            return
        }

        isLoading = true
        // Simulating success
        onSuccess(selectedRole)
        isLoading = false
    }

    fun signInWithGoogle(onSuccess: (UserRole) -> Unit) {
        if (selectedRole == UserRole.NONE) {
            errorMessage = "Please select Artisan or Customer login"
            return
        }
        isLoading = true
        // Placeholder for Google Sign In logic
        // If successful:
        onSuccess(selectedRole)
        isLoading = false
    }

    fun forgotPassword() {
        if (email.isEmpty()) {
            errorMessage = "Please enter your email"
            return
        }
        errorMessage = "Password reset link sent to $email"
    }

    private fun validateInputs(): Boolean {
        if (selectedRole == UserRole.NONE) {
            errorMessage = "Please select Artisan or Customer login"
            return false
        }
        if (email.isEmpty()) {
            errorMessage = "Email cannot be empty"
            return false
        }
        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters"
            return false
        }
        errorMessage = null
        return true
    }
}
