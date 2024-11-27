package com.alcaldiasantaananorte.nortegomotorista.pantallas.login

import android.content.Context
import android.content.IntentFilter
import android.provider.Telephony
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alcaldiasantaananorte.nortegomotorista.R
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.alcaldiasantaananorte.nortegomotorista.componentes.CountdownViewModel
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegomotorista.componentes.ToastType
import com.alcaldiasantaananorte.nortegomotorista.model.rutas.Routes
import com.alcaldiasantaananorte.nortegomotorista.provider.SMSReceiver
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorAzulGob
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun VistaVerificarNumeroView(
    navController: NavHostController,
    identificador: String,
) {
    val auth = FirebaseAuth.getInstance()
    var txtFieldCodigo by remember { mutableStateOf("") }
    val ctx = LocalContext.current

    // Mensajes de error y éxito predefinidos usando stringResource
    val msgCodigoRequerido = stringResource(id = R.string.codigo_requerido)

    val keyboardController = LocalSoftwareKeyboardController.current

    // Estructura del Scaffold
    Scaffold(
        topBar = {
            BarraToolbar(navController, "")
        }
    ) { innerPadding ->
        // Contenido del Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplicar el padding proporcionado por el Scaffold
                .imePadding()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Ajuste a la parte superior
        ) {
            Spacer(modifier = Modifier.height(24.dp)) // Añade espacio adicional si es necesario

           Text(
                text = stringResource(R.string.ingresar_codigo_firebase),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.charla),
                contentDescription = stringResource(id = R.string.logo),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(35.dp))

            // Integrar el detector de SMS
            SMSCodeDetector { detectedCode ->
                txtFieldCodigo = detectedCode
            }

            OtpTextField(codigo = txtFieldCodigo,
                onTextChanged = { newText ->
                txtFieldCodigo = newText
            })

            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = {

                    // Verificar codigo
                    keyboardController?.hide()

                    if(verificarCampos(ctx, txtFieldCodigo, msgCodigoRequerido)){
                        verifyCode(auth, identificador, txtFieldCodigo) { success, errorMessage ->
                            if (success) {
                                navController.navigate(Routes.VistaPrincipal.route) {
                                    popUpTo(0) { // Esto elimina todas las vistas de la pila de navegación
                                        inclusive = true // Asegura que ninguna pantalla anterior quede en la pila
                                    }
                                    launchSingleTop = true // Evita múltiples instancias de la misma vista
                                }
                            } else {
                                CustomToasty(ctx, "Codigo incorrecto", ToastType.ERROR)
                            }
                        }
                    }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorAzulGob,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.verificar),
                    style = TextStyle(fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(35.dp))
        }
    }
}

fun verificarCampos(ctx: Context, txtFieldCodigo: String, msgCodigoRequerido: String): Boolean{
    when {
        txtFieldCodigo.isBlank() -> {
            CustomToasty(ctx, msgCodigoRequerido, ToastType.ERROR)
            return false
        }

        txtFieldCodigo.length < 6 -> {
            CustomToasty(ctx, msgCodigoRequerido, ToastType.ERROR)
            return false
        }

        else -> {
            return true
        }
    }
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
fun SMSCodeDetector(onCodeDetected: (String) -> Unit) {
    val context = LocalContext.current
    val receiver = remember { SMSReceiver() }

    DisposableEffect(context) {
        val intentFilter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        context.registerReceiver(receiver, intentFilter)

        receiver.onCodeReceived = { detectedCode ->
            onCodeDetected(detectedCode)
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraToolbar(navController: NavController, titulo: String) {
    var isNavigating by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            // Usamos un Box para alinear el texto en el centro.
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = titulo,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        },

        navigationIcon = {
            IconButton(
                onClick = {
                    if (!isNavigating) {
                        isNavigating = true
                        navController.popBackStack()
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.volver)
                )
            }
        },
        actions = {
            // Puedes agregar acciones adicionales aquí
        },

        modifier = Modifier.height(56.dp)
    )
}


@Composable
fun OtpTextField(codigo: String, onTextChanged: (String) -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = codigo,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number, // Cambiado a Number para solo números
            imeAction = ImeAction.Done // Para evitar el botón "Enter"
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        onValueChange = { newText ->
            if (newText.length <= 6) {
                onTextChanged(newText)
            }
        },
        singleLine = true
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(6) { index ->
                val number = when {
                    index >= codigo.length -> ""
                    else -> codigo[index]
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(Color.Black)
                    )
                }
            }
        }
    }
}


/*@Preview(showBackground = true)
@Composable
fun PreviewVistaVerificarNumero() {
    val navController = rememberNavController()
    VistaVerificarNumeroView(navController, telefono = "+503 6666-6666", segundos = 20)
}*/