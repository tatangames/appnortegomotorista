package com.alcaldiasantaananorte.nortegomotorista.pantallas.login

import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import com.alcaldiasantaananorte.nortegomotorista.R
import com.alcaldiasantaananorte.nortegomotorista.componentes.BloqueTextFieldLogin
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomModal2Botones
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegomotorista.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegomotorista.componentes.ToastType
import com.alcaldiasantaananorte.nortegomotorista.model.rutas.Routes
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegomotorista.viewmodel.login.LoginViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = viewModel()) {

    val ctx = LocalContext.current
    val isLoading by viewModel.isLoading.observeAsState(true)
    var telefono by remember { mutableStateOf("") }
    val resultado by viewModel.resultado.observeAsState()
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine
    var numerosServer by remember { mutableStateOf(listOf<String>()) }

    // MODAL 1 BOTON
    var showModal1Boton by remember { mutableStateOf(false) }
    var modalMensajeString by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val smsPermission = Manifest.permission.RECEIVE_SMS
    val permissionStateSMS= rememberPermissionState(permission = smsPermission)
    var isLoadingFire by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!permissionStateSMS.status.isGranted) {
            permissionStateSMS.launchPermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.listaTelefonosAutorizadosRX()
        }
    }

    when {
        permissionStateSMS.status.isGranted -> {
            // Si el permiso está otorgado
            //  Log.d("PERMISO", "permisio camara aceptado")
        }
        permissionStateSMS.status.shouldShowRationale -> {
            // Si el usuario rechazó el permiso previamente
            // Log.d("PERMISO", "necesitamos acceso a la camara para continuar")
        }
        else -> {
            // Log.d("PERMISO", "esperando respuesta")
        }
    }


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
                painter = painterResource(id = R.drawable.camion),
                contentDescription = stringResource(id = R.string.logo),
                modifier = Modifier.size(199.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Texto (titulo)
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

            BloqueTextFieldLogin(text = telefono, onTextChanged = { newText ->
                telefono = newText
            })

            Spacer(modifier = Modifier.height(50.dp))

            // Botón de registro
            Button(
                onClick = {

                    keyboardController?.hide()

                    when {
                        telefono.isBlank() -> {
                            modalMensajeString = ctx.getString(R.string.telefono_es_requerido)
                            showModal1Boton = true
                        }

                        telefono.length < 8 -> { // VA SIN GUION
                            modalMensajeString = ctx.getString(R.string.telefono_es_requerido)
                            showModal1Boton = true
                        }
                        else -> {

                            // verificar que este en los numeros permitidos iniciar sesion
                            val numeroEncontrado = numerosServer.any { it.contains(telefono) }

                            if (numeroEncontrado) {

                                isLoadingFire = true

                                val areatel = "+503$telefono"

                                val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                                    .setPhoneNumber(areatel) // Número de teléfono con prefijo (+503, +1, etc.)
                                    .setTimeout(60L, TimeUnit.SECONDS) // Tiempo de espera
                                    .setActivity(Activity()) // Actividad actual
                                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                            // Este método no es necesario para tu caso, puedes dejarlo vacío.
                                        }

                                        override fun onVerificationFailed(e: FirebaseException) {
                                            // Manejar error en el envío del código
                                            Log.e("Auth FIREBASE", "Error al enviar el código: ${e.message}")
                                            isLoadingFire = false

                                            CustomToasty(
                                                ctx,
                                                "Error al enviar el código: ${e.message}",
                                                ToastType.ERROR
                                            )
                                        }

                                        override fun onCodeSent(
                                            verificationId: String,
                                            token: PhoneAuthProvider.ForceResendingToken
                                        ) {
                                            // Código enviado correctamente
                                            Log.d("Auth FIREBASE", "Código enviado correctamente: $verificationId")
                                            isLoadingFire = false

                                            navController.navigate(Routes.VistaVerificarNumero.createRoute(verificationId))
                                        }
                                    })
                                    .build()

                                PhoneAuthProvider.verifyPhoneNumber(options)
                            } else {
                                CustomToasty(ctx, "Número no autorizado", ToastType.ERROR)
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
                    text = stringResource(id = R.string.verificar),
                    fontSize = 18.sp,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        if(showModal1Boton){
            CustomModal1Boton(showModal1Boton, modalMensajeString, onDismiss = {showModal1Boton = false})
        }


        if(isLoadingFire){
            LoadingModal(isLoading = isLoadingFire)
        }

        if (isLoading) {
            LoadingModal(isLoading = isLoading)
        }
    }

    resultado?.getContentIfNotHandled()?.let { result ->

        when (result.success) {

            1 -> {
                numerosServer = result.telefono.map { telItem ->
                    "${telItem.telefono}"
                }
            }
            else -> {
                // Error, mostrar Toast
                CustomToasty(ctx, stringResource(id = R.string.error_reintentar), ToastType.ERROR)
            }
        }
    }
}


fun cambio(){

}














