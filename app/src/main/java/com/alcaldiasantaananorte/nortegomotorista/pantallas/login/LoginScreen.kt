package com.alcaldiasantaananorte.nortegomotorista.pantallas.login

import android.Manifest
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomModal2Botones
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
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit












@Composable
fun PhoneAuthScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    var phoneNumber by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var smsCode by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (!isCodeSent) {
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Número de teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    sendVerificationCode(auth, phoneNumber) { id ->
                        verificationId = id
                        isCodeSent = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar código")
            }
        } else {
            TextField(
                value = smsCode,
                onValueChange = { smsCode = it },
                label = { Text("Código SMS") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    verifyCode(auth, verificationId, smsCode) { success, errorMessage ->
                        if (success) {
                            message = "¡Autenticación exitosa!"
                        } else {
                            message = errorMessage ?: "Error desconocido"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verificar código")
            }
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message)
        }
    }
}


fun sendVerificationCode(
    auth: FirebaseAuth,
    phoneNumber: String,
    onCodeSent: (String) -> Unit
) {
    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(phoneNumber)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(Activity())
        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Autenticación automática (cuando es posible)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Manejar errores aquí
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                onCodeSent(verificationId)
            }
        }).build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}

fun verifyCode(
    auth: FirebaseAuth,
    verificationId: String?,
    code: String,
    onResult: (Boolean, String?) -> Unit
) {
    val credential = PhoneAuthProvider.getCredential(verificationId ?: "", code)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null)
            } else {
                onResult(false, task.exception?.message)
            }
        }
}









@Composable
fun LoginScreen(navController: NavHostController, auth: FirebaseAuth) {

    val ctx = LocalContext.current
    var telefono by remember { mutableStateOf("") }
    var txtFieldNumero by remember { mutableStateOf(telefono) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isLoading by remember { mutableStateOf(false) }
    var modalMensajeString by remember { mutableStateOf("") }
    var showModal1Boton by remember { mutableStateOf(false) }
    var showModal2Boton by remember { mutableStateOf(false) }

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


            BloqueTextFieldLogin(text = txtFieldNumero, onTextChanged = { newText ->
                txtFieldNumero = newText
                telefono = newText
            })

            Spacer(modifier = Modifier.height(50.dp))

            // Botón de registro
            Button(
                onClick = {

                    keyboardController?.hide()

                    when {
                        txtFieldNumero.isBlank() -> {
                            modalMensajeString = ctx.getString(R.string.telefono_es_requerido)
                            showModal1Boton = true
                        }

                        txtFieldNumero.length < 8 -> { // VA SIN GUION
                            modalMensajeString = ctx.getString(R.string.telefono_es_requerido)
                            showModal1Boton = true
                        }
                        else -> {
                            // abrir modal para mostrarle al usuario si el numero es correcto
                            showModal2Boton = true
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
                    text = stringResource(id = R.string.verificar),
                    fontSize = 18.sp,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            Spacer(modifier = Modifier.height(100.dp))



            if (isLoading) {
                LoadingModal(isLoading = isLoading)
            }

            if(showModal2Boton){
                CustomModal2Botones(
                    showDialog = true,
                    message = stringResource(id = R.string.verificar_numero_introducido, telefono),
                    onDismiss = { showModal2Boton = false },
                    onAccept = {

                        val areatel = "+503$telefono"

                        showModal2Boton = false
                        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                            .setPhoneNumber(areatel) // Número de teléfono con prefijo (+503, +1, etc.)
                            .setTimeout(60L, TimeUnit.SECONDS) // Tiempo de espera
                            .setActivity(Activity()) // Actividad actual
                            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    // Si la verificación se completa automáticamente (auto-retrieval)
                                    FirebaseAuth.getInstance().signInWithCredential(credential)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Usuario autenticado con éxito
                                                Log.d("Auth FIREBASE", "Usuario autenticado correctamente")
                                            } else {
                                                Log.e("Auth FIREBASE", "Error al autenticar", task.exception)
                                                CustomToasty(
                                                    ctx,
                                                    "Error al verificar",
                                                    ToastType.ERROR
                                                )
                                            }
                                        }
                                }

                                override fun onVerificationFailed(e: FirebaseException) {
                                    // Error en la verificación
                                    Log.e("Auth FIREBASE", "Verificación fallida: ${e.message}")
                                    CustomToasty(
                                        ctx,
                                        "Error al verificar",
                                        ToastType.ERROR
                                    )
                                }

                                override fun onCodeSent(
                                    verificationId: String,
                                    token: PhoneAuthProvider.ForceResendingToken
                                ) {
                                    // Código enviado, guarda el `verificationId` para usarlo en la verificación manual
                                    Log.d("Auth FIREBASE", "Código enviado: $verificationId")
                                }
                            })
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    }
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }



}

