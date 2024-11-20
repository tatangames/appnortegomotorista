package com.alcaldiasantaananorte.nortegomotorista.pantallas.login

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.alcaldiasantaananorte.nortegomotorista.R
import com.alcaldiasantaananorte.nortegomotorista.componentes.BloqueTextFieldLogin
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegomotorista.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegomotorista.componentes.ToastType
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.BackgroundButton
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.Black
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.Gray
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.Green
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ShapeButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.delay


@Composable
fun LoginScreen(navController: NavHostController, auth: FirebaseAuth) {

    val ctx = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    var isLoading by remember { mutableStateOf(false) }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 25.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Imagen (logotipo)
            Image(
                painter = painterResource(id = R.drawable.logofinal),
                contentDescription = stringResource(id = R.string.logo),
                modifier = Modifier.size(199.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(30.dp))


            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 27.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
            )

            Spacer(modifier = Modifier.height(20.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Campo de texto para correo
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    placeholder = { Text("usuario@ejemplo.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "Correo")
                    }
                )

                // Campo de texto para contraseña
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    placeholder = { Text("Ingresa tu contraseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Contraseña")
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    }
                )
            }


            Spacer(modifier = Modifier.height(50.dp))

            // Botón de login
            Button(
                onClick = {

                    when {
                        email.isBlank() -> {
                            CustomToasty(ctx, "Correo es requerido", ToastType.ERROR)
                        }

                        password.isBlank() -> {
                            CustomToasty(ctx, "Correo es requerido", ToastType.ERROR)
                        }

                        password.length < 6 -> {
                            CustomToasty(ctx, "Contraseña mínimo 6 caracteres", ToastType.ERROR)
                        }

                        else -> {
                            isLoading = true

                            auth.signInWithEmailAndPassword(email, password)
                                .addOnFailureListener { exception ->
                                    isLoading = false
                                    when (exception) {
                                        is FirebaseAuthInvalidCredentialsException -> {
                                            // Contraseña incorrecta

                                            CustomToasty(ctx, "Credenciales incorrecta", ToastType.INFO)
                                        }
                                        is FirebaseAuthInvalidUserException -> {
                                            // Usuario no registrado
                                            CustomToasty(ctx, "Credenciales incorrecta", ToastType.INFO)
                                        }
                                    }
                                }
                                .addOnCompleteListener { task ->

                                    isLoading = false

                                    if (task.isSuccessful) {
                                        val user = task.result?.user
                                        // Inicio de sesión exitoso
                                        CustomToasty(ctx, "inicio bien", ToastType.INFO)

                                    } else {
                                        val error = task.exception?.message
                                        // Manejar error
                                        CustomToasty(ctx, "Credenciales incorrecta", ToastType.INFO)
                                    }
                                }
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorAzulGob,
                    contentColor = ColorBlancoGob
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.acceder),
                    fontSize = 18.sp,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            if (isLoading) {
                LoadingModal(isLoading = isLoading)
            } else {
                Text("Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(100.dp))
        }






    }



}

