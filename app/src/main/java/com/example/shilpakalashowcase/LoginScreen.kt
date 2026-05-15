package com.example.shilpakalashowcase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Cream = Color(0xFFFFF8EA)
private val DeepBrown = Color(0xFF4B2E1F)
private val Gold = Color(0xFFD4A017)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToArtisan: () -> Unit,
    onNavigateToCustomer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Cream
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // App Header
            Text(
                text = "Shilpa-Kala Showcase",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBrown
            )
            Text(
                text = "Ancient Art, Modern Reach",
                fontSize = 16.sp,
                color = DeepBrown.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Role Selection
            Text(
                text = "Choose Your Role",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBrown,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoleCard(
                    title = "Artisan Login",
                    isSelected = viewModel.selectedRole == UserRole.ARTISAN,
                    onClick = { viewModel.selectedRole = UserRole.ARTISAN },
                    modifier = Modifier.weight(1f)
                )
                RoleCard(
                    title = "Customer Login",
                    isSelected = viewModel.selectedRole == UserRole.CUSTOMER,
                    onClick = { viewModel.selectedRole = UserRole.CUSTOMER },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Form Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (viewModel.isSignUpMode) "Create Account" else "Welcome Back",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepBrown
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = DeepBrown.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = DeepBrown.copy(alpha = 0.3f)
                        )
                    )

                    AnimatedVisibility(visible = viewModel.isSignUpMode) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = viewModel.confirmPassword,
                                onValueChange = { viewModel.confirmPassword = it },
                                label = { Text("Confirm Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Gold,
                                    unfocusedBorderColor = DeepBrown.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }

                    if (!viewModel.isSignUpMode) {
                        TextButton(
                            onClick = { viewModel.forgotPassword() },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Forgot Password?", color = DeepBrown, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (viewModel.isSignUpMode) {
                                viewModel.signUpWithEmail { role ->
                                    if (role == UserRole.ARTISAN) onNavigateToArtisan() else onNavigateToCustomer()
                                }
                            } else {
                                viewModel.signInWithEmail { role ->
                                    if (role == UserRole.ARTISAN) onNavigateToArtisan() else onNavigateToCustomer()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepBrown),
                        enabled = !viewModel.isLoading
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (viewModel.isSignUpMode) "Sign Up" else "Login", fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign In
            OutlinedButton(
                onClick = {
                    viewModel.signInWithGoogle { role ->
                        if (role == UserRole.ARTISAN) onNavigateToArtisan() else onNavigateToCustomer()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, DeepBrown.copy(alpha = 0.2f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Continue with Google", color = DeepBrown)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (viewModel.isSignUpMode) "Already have an account? " else "Don't have an account? ",
                    color = DeepBrown
                )
                Text(
                    text = if (viewModel.isSignUpMode) "Login" else "Sign Up",
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.isSignUpMode = !viewModel.isSignUpMode }
                )
            }
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DeepBrown else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
            .clickable { onClick() }
            .border(
                width = 2.dp,
                color = if (isSelected) Gold else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (isSelected) Gold else DeepBrown,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = if (isSelected) Color.White else DeepBrown,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
