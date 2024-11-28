package com.alcaldiasantaananorte.nortegomotorista.pantallas.principal

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alcaldiasantaananorte.nortegomotorista.R
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegomotorista.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegomotorista.componentes.ToastType
import com.alcaldiasantaananorte.nortegomotorista.provider.AuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Opcion(val id: Int, val nombre: String)
val opcionesMunicipios = listOf(
    Opcion(1, "Metapán"),
    Opcion(2, "Texistepeque"),
    Opcion(3, "Santa Rosa"),
    Opcion(4, "Masahuat")
)

data class Driver(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val tipo: String
)
{
    // Constructor vacío requerido por Firebase Firestore
    constructor() : this("", "", "", "")
}

@Composable
fun PerfilScreen(navController: NavHostController){
    val ctx = LocalContext.current

    // MODAL 1 BOTON

    val keyboardController = LocalSoftwareKeyboardController.current
    var isLoadingFire by remember { mutableStateOf(true) }

    // CUANDO YA ESTA REGISTRADO
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }

    // CUANDO SERA NUEVO REGISTRO
    var nombreRegistro by remember { mutableStateOf(TextFieldValue()) }
    var descripcionRegistro by remember { mutableStateOf(TextFieldValue()) }



    var pantallaCargada by remember { mutableStateOf(false) }

    var driver by remember { mutableStateOf<Driver?>(null) }
    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid


    if (currentUserId != null) {

        // BUSCAR MI DRIVER DE LA BASE DE DATOS FIREBASE
        LaunchedEffect(Unit) {
            currentUserId.let { userId ->
                val driverDoc = db.collection("Drivers").document(userId).get().await()
                if (driverDoc.exists()) {
                    val fetchedDriver = driverDoc.toObject(Driver::class.java)
                    fetchedDriver?.let {
                        driver = it
                        nombre = it.nombre
                        descripcion = it.descripcion
                        tipo = it.tipo
                    }
                }

                isLoadingFire = false
                pantallaCargada = true
            }
        }

        // Si ya está registrado, muestra sus datos
        if(pantallaCargada){
            if (driver != null) {

                // NUEVO REGISTRO DE PERFIL

                DriverProfile(driver = driver!!) { updatedNombre, updatedDescripcion, updateTipo,  ->
                    // Actualizar datos en Firestore

                    if(updatedNombre.isBlank()){
                        CustomToasty(ctx, "Nombre es requerido", ToastType.INFO)
                        return@DriverProfile
                    }

                    if(updatedDescripcion.isBlank()){
                        CustomToasty(ctx, "Descripción es requerido", ToastType.INFO)
                        return@DriverProfile
                    }

                    if(updateTipo.isBlank()){
                        CustomToasty(ctx, "Tipo es requerido", ToastType.INFO)
                        return@DriverProfile
                    }

                    isLoadingFire = true

                    currentUserId.let {
                        db.collection("Drivers").document(it)
                            .set(Driver(id = it, nombre = updatedNombre, descripcion = updatedDescripcion, tipo = updateTipo))
                            .addOnSuccessListener {
                                isLoadingFire = false
                                CustomToasty(ctx, "Perfil actualizado", ToastType.SUCCESS)
                            }
                            .addOnFailureListener {
                                isLoadingFire = false
                                CustomToasty(ctx, "Error al actualizar", ToastType.ERROR)
                            }
                    }
                }
            } else {

                // FORMULARIO DE REGISTRO
                var seleccionadaRegistro by remember { mutableStateOf<Opcion?>(null) }

                RegistrationForm(
                    nombre = nombreRegistro,
                    descripcion = descripcionRegistro,
                    onNameChange = { nombreRegistro = it },
                    onDescripcionChange = { descripcionRegistro = it },
                    onOptionSelected = { opcion -> seleccionadaRegistro = opcion },
                    onRegister = {
                        currentUserId.let {

                            if(nombreRegistro.text.isBlank()){
                                CustomToasty(ctx, "Nombre es requerido", ToastType.INFO)
                                return@let
                            }

                            if(descripcionRegistro.text.isBlank()){
                                CustomToasty(ctx, "Descripción es requerido", ToastType.INFO)
                                return@let
                            }

                            val newDriver = Driver(id = it,
                                nombre = nombreRegistro.text,
                                descripcion = descripcionRegistro.text,
                                tipo = seleccionadaRegistro?.id?.toString() ?: ""
                            )

                            isLoadingFire = true

                            // REGISTRAR PERFIL EN FIREBASE
                            db.collection("Drivers").document(it).set(newDriver)
                                .addOnSuccessListener {

                                    isLoadingFire = false

                                    CustomToasty(ctx, "Perfil Registrado", ToastType.SUCCESS)
                                    // salir de la pantalla
                                    navController.popBackStack()

                                }
                                .addOnFailureListener {

                                    isLoadingFire = false

                                    CustomToasty(ctx, "Error al registrar", ToastType.ERROR)
                                }
                        }
                    }
                )
            }
        }

    }

    if (isLoadingFire) {
        LoadingModal(isLoading = isLoadingFire)
    }
}


@Composable
fun RegistrationForm(nombre: TextFieldValue, descripcion: TextFieldValue,
                     onNameChange: (TextFieldValue) -> Unit,
                     onDescripcionChange: (TextFieldValue) -> Unit,
                     onOptionSelected: (Opcion?) -> Unit,
                     onRegister: () -> Unit,
                     ) {

    var seleccionada by remember { mutableStateOf<Opcion?>(null) }
    val ctx = LocalContext.current

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(40.dp))

        MunicipalSelect(
            opciones = opcionesMunicipios,
            seleccionada = seleccionada,
            onOptionSelected = { opcion ->
                seleccionada = opcion
                onOptionSelected(opcion)
            }
        )

        Spacer(modifier = Modifier.height(35.dp))
        TextField(
            value = nombre,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = onNameChange,
            label = { Text("Nombre Recolector") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            placeholder = { Text("Nombre Recolector") },
            singleLine = true,
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
            onValueChange = onDescripcionChange,
            label = { Text("Descripción Recolector") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            placeholder = { Text("Descripción Recolector") },
            singleLine = true,
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



        Button(onClick = {
            if(seleccionada != null){
                onRegister()
            }else{
                CustomToasty(ctx, "Seleccionar Tipo", ToastType.INFO)
            }
        }) {
            Text("Registrar")
        }
    }
}


@Composable
fun DriverProfile(driver: Driver, onUpdate: (String, String, String) -> Unit) {
    var nombre by remember { mutableStateOf(driver.nombre) }
    var descripcion by remember { mutableStateOf(driver.descripcion) }
    var tipo by remember { mutableStateOf(driver.tipo) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var seleccionada by remember { mutableStateOf<Opcion?>(null) }
        // Inicializar la selección con base en el string inicial
        LaunchedEffect(driver.tipo) {
            val selectedId = driver.tipo.toIntOrNull()
            seleccionada = opcionesMunicipios.find { it.id == selectedId }
        }


        Spacer(modifier = Modifier.height(40.dp))

        MunicipalSelect(
            opciones = opcionesMunicipios,
            seleccionada = seleccionada,
            onOptionSelected = { opcion ->
                seleccionada = opcion
            }
        )

        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            value = nombre,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { nuevoNombre ->
                    nombre = nuevoNombre
            },
            label = { Text("Nombre Recolector") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            placeholder = { Text("Nombre Recolector") },
            singleLine = true,
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


        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            value = descripcion,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { nuevoNombre ->
                descripcion = nuevoNombre
            },
            label = { Text("Descripcion Recolector") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            placeholder = { Text("Descripcion Recolector") },
            singleLine = true,
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


        Spacer(modifier = Modifier.height(45.dp))

        Button(onClick = {
            onUpdate(nombre, descripcion, seleccionada?.id.toString()) }
        ) {
            Text("Actualizar")
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







