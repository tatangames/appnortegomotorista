package com.alcaldiasantaananorte.nortegomotorista.pantallas.principal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.alcaldiasantaananorte.nortegomotorista.R
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomModalCerrarSesion
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegomotorista.componentes.DrawerBody
import com.alcaldiasantaananorte.nortegomotorista.componentes.DrawerHeader
import com.alcaldiasantaananorte.nortegomotorista.componentes.LoadingModal
import com.alcaldiasantaananorte.nortegomotorista.componentes.ToastType
import com.alcaldiasantaananorte.nortegomotorista.componentes.itemsMenu
import com.alcaldiasantaananorte.nortegomotorista.model.rutas.Routes
import com.alcaldiasantaananorte.nortegomotorista.permisos.SolicitarPermisosUbicacion
import com.alcaldiasantaananorte.nortegomotorista.provider.AuthProvider
import com.alcaldiasantaananorte.nortegomotorista.provider.GeoProvider
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorAzulGob
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorBlancoGob
import com.alcaldiasantaananorte.nortegomotorista.ui.theme.ColorGris1Gob
import com.alcaldiasantaananorte.nortegomotorista.utils.TokenManager
import com.alcaldiasantaananorte.nortegomotorista.viewmodel.perfil.PerfilViewModel
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.Listener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    navController: NavHostController,
    viewModel: PerfilViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showModalCerrarSesion by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine
    var popPermisoGPS by remember { mutableStateOf(false) }
    val authProvider = AuthProvider()
    val tokenManager = remember { TokenManager(ctx) }
    val isLoading by viewModel.isLoading.observeAsState(true)
    val resultado by viewModel.resultado.observeAsState()

    var boolServerCargado by remember { mutableStateOf(false) }
    var boolPermisoServer by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        scope.launch {
            val tel = tokenManager.telefonoToken.first()
            viewModel.permisoMotorista(telefono = tel)
        }
    }

    //  ES PARA VERIFICAR PERMISOS DE UBICACION CUANDO SE CARGUE LA PANTALLA
    SolicitarPermisosUbicacion(
        onPermisosConcedidos = { },
        onPermisosDenegados = { }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                DrawerBody(items = itemsMenu) { item ->
                    when (item.id) {
                        1 -> {

                            navController.navigate(Routes.VistaPerfil.route) {
                                navOptions {
                                    launchSingleTop = true
                                }
                            }
                        }

                        2 -> {
                            // cerrar sesion
                            showModalCerrarSesion = true
                        }
                    }

                    scope.launch {
                        drawerState.close()
                    }
                }
            }
        }
    ) {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.mapa),
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Gray,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues  ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Respeta el padding del Scaffold
                contentAlignment = Alignment.Center
            ) {

                if(boolServerCargado){
                    if(boolPermisoServer){










                    }else{
                        Text(
                            text = "Habilitar acceso en Servidor, cuando ya se haya creado un Perfil al Motorista",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp) // Padding a los lados
                        )
                    }
                }
            }
        }

        if (showModalCerrarSesion) {
            CustomModalCerrarSesion(showModalCerrarSesion,
                stringResource(R.string.cerrar_sesion),
                onDismiss = { showModalCerrarSesion = false },
                onAccept = {
                    scope.launch {
                        authProvider.cerrarSesion()
                        showModalCerrarSesion = false
                        navigateToLogin(navController)
                    }
                })
        }

        if(popPermisoGPS){
            AlertDialog(
                onDismissRequest = { popPermisoGPS = false },
                title = { Text(stringResource(R.string.permiso_gps_requerido)) },
                text = { Text(stringResource(R.string.para_usar_esta_funcion_gps)) },
                confirmButton = {
                    Button(
                        onClick = {
                            popPermisoGPS = false
                            redireccionarAjustes(ctx)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorAzulGob,
                            contentColor = ColorBlancoGob
                        )
                    ){
                        Text(stringResource(R.string.ir_a_ajustes))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            popPermisoGPS = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorGris1Gob,
                            contentColor = ColorBlancoGob
                        )
                    ){
                        Text(stringResource(R.string.cancelar))
                    }
                }
            )
        }

        if (isLoading) {
            LoadingModal(isLoading = isLoading)
        }
    }


    resultado?.getContentIfNotHandled()?.let { result ->

        when (result.success) {
            1 -> {
                boolServerCargado = true
                if(result.registrado == 1){
                    boolPermisoServer = true
                }

            }
            else -> {
                // Error, mostrar Toast
                CustomToasty(ctx, stringResource(id = R.string.error_reintentar), ToastType.ERROR)
            }
        }
    }
}


fun redireccionarAjustes(context: Context){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}


// redireccionar a vista login
private fun navigateToLogin(navController: NavHostController) {
    navController.navigate(Routes.VistaLogin.route) {
        popUpTo(Routes.VistaPrincipal.route) {
            inclusive = true // Elimina VistaPrincipal de la pila
        }
        launchSingleTop = true // Asegura que no se creen múltiples instancias de VistaLogin
    }
}





class LocationViewModel(
    private val geoProvider: GeoProvider,
    private val authProvider: AuthProvider,
    private val easyWayLocation: EasyWayLocation
) : ViewModel() {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    fun checkDriverConnection() {
        geoProvider.getLocation(authProvider.getId()).addOnSuccessListener { document ->
            _isConnected.value = document.exists() && document.contains("l")
        }
    }

    fun connectDriver() {
        easyWayLocation.startLocation()
        _isConnected.value = true
    }

    fun disconnectDriver() {
        easyWayLocation.endUpdates()
        _currentLocation.value?.let { location ->
            geoProvider.removeLocation(authProvider.getId())
            _isConnected.value = false
        }
    }

    fun saveLocation(location: LatLng) {
        geoProvider.saveLocation(authProvider.getId(), location)
        _currentLocation.value = location
    }

    fun handleLocationUpdate(location: Location) {
        val newLocation = LatLng(location.latitude, location.longitude)
        saveLocation(newLocation)
    }
}

@Composable
fun LocationTrackingScreen(
    viewModel: LocationViewModel,
    easyWayLocation: EasyWayLocation
) {
    val context = LocalContext.current
    val isConnected by viewModel.isConnected.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        when {
            fineLocationGranted -> {
                Log.d("LOCALIZACION", "Permiso aceptado")
                viewModel.checkDriverConnection()
            }
            coarseLocationGranted -> {
                Log.d("LOCALIZACION", "Permiso concedido con limitación")
                viewModel.checkDriverConnection()
            }
            else -> {
                Log.d("LOCALIZACION", "Permiso no aceptado")
            }
        }
    }

    // Simular configuración de EasyWayLocation similar al original
    LaunchedEffect(Unit) {
        val locationRequest = LocationRequest.create().apply {
            interval = 0
            fastestInterval = 0
            priority = Priority.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = 1f
        }

       /* easyWayLocation.setLocationListener(object : Listener {
            override fun currentLocation(location: Location) {
                viewModel.handleLocationUpdate(location)
            }

            override fun locationOn() {}
            override fun locationCancelled() {}
        })*/

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (isConnected) "Conectado" else "Desconectado",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        currentLocation?.let { location ->
            Text(
                text = "Ubicación actual: ${location.latitude}, ${location.longitude}",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Row {
            Button(
                onClick = { viewModel.connectDriver() },
                modifier = Modifier.padding(end = 8.dp),
                enabled = !isConnected
            ) {
                Text("Conectar")
            }

            Button(
                onClick = { viewModel.disconnectDriver() },
                enabled = isConnected
            ) {
                Text("Desconectar")
            }
        }
    }
}














