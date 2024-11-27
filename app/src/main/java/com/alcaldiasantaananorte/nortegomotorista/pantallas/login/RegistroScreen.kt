package com.alcaldiasantaananorte.nortegomotorista.pantallas.login

import android.Manifest
import android.app.Activity
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alcaldiasantaananorte.nortegomotorista.R
import com.alcaldiasantaananorte.nortegomotorista.componentes.BloqueTextFieldLogin
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomModal1Boton
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegomotorista.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegomotorista.componentes.ToastType
import com.alcaldiasantaananorte.nortegomotorista.model.datos.Driver
import com.alcaldiasantaananorte.nortegomotorista.model.datos.Telefono
import com.alcaldiasantaananorte.nortegomotorista.model.rutas.Routes
import com.alcaldiasantaananorte.nortegomotorista.provider.AuthProvider
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorBlancoGob

data class Opcion(val id: Int, val nombre: String)


@Composable
fun RegistroScreen(navController: NavHostController, telefono: String, identificador: String){
    val ctx = LocalContext.current

    // MODAL 1 BOTON
    var showModal1Boton by remember { mutableStateOf(false) }
    var modalMensajeString by remember { mutableStateOf("") }


    val keyboardController = LocalSoftwareKeyboardController.current
    var isLoadingFire by remember { mutableStateOf(false) }

    // Datos motoristas
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val authProvider: AuthProvider

    val opciones = listOf(
        Opcion(1, "Metapán"),
        Opcion(2, "Texistepeque"),
        Opcion(3, "Santa Rosa"),
        Opcion(4, "Masahuat")
    )

    var seleccionada by remember { mutableStateOf<Opcion?>(null) }

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


            TextField(
                value = nombre,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { nuevoNombre ->
                    if (nuevoNombre.length <= 100) {  // Set max length to 100 characters
                        nombre = nuevoNombre
                    }
                },
                label = { Text("Nombre Recolector") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                placeholder = { Text("Nombre Recolector") },
                singleLine = true,
                supportingText = {
                    Text("${nombre.length}/50")  // Optional: show character count
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    errorContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black,
                    focusedLabelColor = Color.Black, // Color del label cuando está enfocado
                    unfocusedLabelColor = Color.Black // Color del label cuando no está enfocado
                ),
            )
            Spacer(modifier = Modifier.height(35.dp))


            TextField(
                value = descripcion,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { nuevoNombre ->
                    if (nuevoNombre.length <= 100) {  // Set max length to 100 characters
                        descripcion = nuevoNombre
                    }
                },
                label = { Text("Descripción Recolector") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                placeholder = { Text("Descripción Recolector") },
                singleLine = true,
                supportingText = {
                    Text("${descripcion.length}/50")  // Optional: show character count
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    errorContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black,
                    focusedLabelColor = Color.Black, // Color del label cuando está enfocado
                    unfocusedLabelColor = Color.Black // Color del label cuando no está enfocado
                ),
            )
            Spacer(modifier = Modifier.height(35.dp))



            MunicipalSelect(
                opciones = opciones,
                seleccionada = seleccionada,
                onOptionSelected = { opcion -> seleccionada = opcion }
            )

            Spacer(modifier = Modifier.height(45.dp))



            // Botón de registro
            Button(
                onClick = {
                    keyboardController?.hide()

                    if(nombre.isBlank()){
                        CustomToasty(ctx, "Ingresa un nombre", ToastType.ERROR)
                        return@Button
                    }

                    if(descripcion.isBlank()){
                        CustomToasty(ctx, "Ingresa una descripción", ToastType.ERROR)
                        return@Button
                    }

                    if(seleccionada == null){
                        CustomToasty(ctx, "Selecciona un municipio", ToastType.ERROR)
                        return@Button
                    }

                    // REGISTRO FIREBASE



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

        if(isLoadingFire) {
            LoadingModal(isLoading = isLoadingFire)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MunicipalSelect(
    opciones: List<Opcion>,
    seleccionada: Opcion?,
    onOptionSelected: (Opcion) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = seleccionada?.nombre ?: "Seleccionar municipio",
                onValueChange = {},
                readOnly = true,
                label = { Text("Municipio") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion.nombre) },
                        onClick = {
                            onOptionSelected(opcion)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}







