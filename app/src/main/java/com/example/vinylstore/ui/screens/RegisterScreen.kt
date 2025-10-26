package com.example.vinylstore.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vinylstore.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var nombreError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    val registerState = viewModel.registerState.collectAsState()
    
    LaunchedEffect(registerState.value) {
        when (registerState.value) {
            is AuthViewModel.RegisterState.Success -> {
                onRegisterSuccess()
            }
            is AuthViewModel.RegisterState.Error -> {
                //mostrar error
            }
            else -> {}
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = nombre,
            onValueChange = { newValue ->
                nombre = newValue
                val validation = viewModel.validateName(nombre)
                nombreError = when (validation) {
                    is AuthViewModel.ValidationState.Valid -> null
                    is AuthViewModel.ValidationState.Invalid -> null
                }
            },
            label = { Text("Nombre") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            isError = nombreError != null,
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        )
        
        if (nombreError != null) {
            Text(
                text = nombreError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { newValue ->
                email = newValue
                val validation = viewModel.validateEmail(email)
                emailError = when (validation) {
                    is AuthViewModel.ValidationState.Valid -> null
                    is AuthViewModel.ValidationState.Invalid -> null
                }
            },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
            isError = emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        )
        
        if (emailError != null) {
            Text(
                text = emailError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { newValue ->
                password = newValue
                val validation = viewModel.validatePassword(password)
                passwordError = when (validation) {
                    is AuthViewModel.ValidationState.Valid -> null
                    is AuthViewModel.ValidationState.Invalid -> null
                }
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        )
        
        if (passwordError != null) {
            Text(
                text = passwordError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                nombreError = null
                emailError = null
                passwordError = null
                
                val nombreValidation = viewModel.validateName(nombre)
                if (nombreValidation is AuthViewModel.ValidationState.Invalid) {
                    nombreError = nombreValidation.message
                    return@Button
                }
                
                val emailValidation = viewModel.validateEmail(email)
                if (emailValidation is AuthViewModel.ValidationState.Invalid) {
                    emailError = emailValidation.message
                    return@Button
                }
                
                val passwordValidation = viewModel.validatePassword(password)
                if (passwordValidation is AuthViewModel.ValidationState.Invalid) {
                    passwordError = passwordValidation.message
                    return@Button
                }
                
                viewModel.register(nombre, email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = registerState.value !is AuthViewModel.RegisterState.Loading
        ) {
            if (registerState.value is AuthViewModel.RegisterState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Registrarse")
            }
        }
        
        if (registerState.value is AuthViewModel.RegisterState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (registerState.value as AuthViewModel.RegisterState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
