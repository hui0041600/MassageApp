package com.example.massageapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isRegistering by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            if (isRegistering) "Create a New Account" else "Login to Your Account",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val emailStr = email.text.trim()
                val passStr = password.text.trim()

                if (emailStr.isEmpty() || passStr.isEmpty()) {
                    Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (isRegistering) {
                    AuthService.register(
                        emailStr, passStr,
                        onSuccess = {
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                            navigateAfterLogin(navController, emailStr)
                        },
                        onFailure = {
                            Toast.makeText(context, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    AuthService.login(
                        emailStr, passStr,
                        onSuccess = {
                            navigateAfterLogin(navController, emailStr)
                        },
                        onFailure = {
                            Toast.makeText(context, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRegistering) "Register" else "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { isRegistering = !isRegistering }
        ) {
            Text(
                if (isRegistering) "Already have an account? Login"
                else "Don't have an account? Register"
            )
        }
    }
}

private fun navigateAfterLogin(navController: NavController, email: String) {
    if (email.contains("admin")) {
        navController.navigate("admin") {
            popUpTo("login") { inclusive = true }
        }
    } else {
        navController.navigate("main") {
            popUpTo("login") { inclusive = true }
        }
    }
}