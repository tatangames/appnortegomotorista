package com.alcaldiasantaananorte.nortegomotorista.pantallas.principal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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



                        val currentUserId = authProvider.getId()


                        LocationTrackingScreen(authProvider = currentUserId)







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

                Log.d("RESULTADO", result.toString())

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







/*

@Composable
fun DriverLocationScreen(
    geoProvider: GeoProvider,
    authProvider: AuthProvider
) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val isConnected = remember { mutableStateOf(false) }
    val locationState = remember { mutableStateOf(LatLng(0.0, 0.0)) }

    // Permisos de ubicación
    val locationPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Log.d("LOCALIZACION", "Permiso aceptado")
                checkIfDriverIsConnected(geoProvider, authProvider, isConnected)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d("LOCALIZACION", "Permiso concedido con limitación")
                checkIfDriverIsConnected(geoProvider, authProvider, isConnected)
            }
            else -> {
                Log.d("LOCALIZACION", "Permiso no aceptado")
            }
        }
    }

    // Solicitar permisos al iniciar
    LaunchedEffect(Unit) {
        locationPermissions.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    // UI
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (isConnected.value) {
                    disconnectDriver(geoProvider, authProvider, isConnected)
                } else {
                    connectDriver(fusedLocationClient, geoProvider, authProvider, locationState, isConnected, context)
                }
            }
        ) {
            Text(if (isConnected.value) "Desconectar" else "Conectar")
        }
    }
}

private fun checkIfDriverIsConnected(
    geoProvider: GeoProvider,
    authProvider: AuthProvider,
    isConnected: MutableState<Boolean>
) {
    geoProvider.getLocation(authProvider.getId()).addOnSuccessListener { document ->
        if (document.exists() && document.contains("l")) {
            isConnected.value = true
        } else {
            isConnected.value = false
        }
    }.addOnFailureListener {
        Log.e("ERROR", "Error al obtener la ubicación", it)
        isConnected.value = false
    }
}

private fun connectDriver(
    fusedLocationClient: FusedLocationProviderClient,
    geoProvider: GeoProvider,
    authProvider: AuthProvider,
    locationState: MutableState<LatLng>,
    isConnected: MutableState<Boolean>,
    context: Context
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            locationState.value = latLng
            geoProvider.saveLocation(authProvider.getId(), latLng)
            isConnected.value = true
        } else {
            Log.e("ERROR", "No se pudo obtener la ubicación")
        }
    }
}

private fun disconnectDriver(
    geoProvider: GeoProvider,
    authProvider: AuthProvider,
    isConnected: MutableState<Boolean>
) {
    geoProvider.removeLocation(authProvider.getId()).addOnSuccessListener {
        isConnected.value = false
    }.addOnFailureListener {
        Log.e("ERROR", "Error al desconectar", it)
    }
}







@Composable
fun MapScreen() {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Para guardar la ubicación en Firebase
    val userId = auth.currentUser?.uid ?: return
    val locationState = remember { mutableStateOf(LatLng(0.0, 0.0)) }

    // Solicitar permisos de ubicación
    val locationPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            getLastKnownLocation(fusedLocationClient, locationState, ctx = context)
        }
    }

    // Comprobar permisos
    LaunchedEffect(Unit) {
        locationPermissions.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    // Mapa
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = CameraPositionState(
            position = CameraPosition.fromLatLngZoom(locationState.value, 15f)
        )
    ) {
        Marker(
            state = MarkerState(position = locationState.value),
            title = "Mi Ubicación"
        )
    }

    // Guardar ubicación en Firebase en tiempo real
    LaunchedEffect(locationState.value) {
        val locationData = mapOf(
            "latitude" to locationState.value.latitude,
            "longitude" to locationState.value.longitude
        )
        firestore.collection("Drivers").document(userId)
            .set(locationData, SetOptions.merge())
    }
}



private fun getLastKnownLocation(fusedLocationClient: FusedLocationProviderClient,
                                 locationState: MutableState<LatLng>,
                                 ctx: Context) {
    if (ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            locationState.value = LatLng(location.latitude, location.longitude)
        }
    }
}


*/













class LocationManager(private val authProvider: String) {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    private val geoProvider = GeoProvider()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    fun initLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun connectDriver(context: Context) {
        try {
            // Configura la solicitud de ubicación para permitir ubicaciones simuladas
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build()

            // Configura el callback para recibir actualizaciones de ubicación
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val latLng = LatLng(location.latitude, location.longitude)

                        // Actualiza la ubicación actual
                        _currentLocation.value = latLng

                        // Guarda la ubicación en Firebase
                        CoroutineScope(Dispatchers.IO).launch {
                            geoProvider.saveLocation(authProvider, latLng)
                        }
                    }
                }
            }

            // Solicita actualizaciones de ubicación
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )

            _isConnected.value = true
        } catch (e: SecurityException) {
            // Maneja errores de permisos
            e.printStackTrace()
        }
    }

    suspend fun disconnectDriver() {
        // Detiene las actualizaciones de ubicación
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }

        // Elimina la ubicación de Firebase
        geoProvider.removeLocation(authProvider)

        // Resetea los estados
        _isConnected.value = false
        _currentLocation.value = null
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationTrackingScreen(authProvider: String) {
    val context = LocalContext.current
    val locationManager = remember { LocationManager(authProvider) }

    // Estado de permisos de ubicación
    val locationPermission = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Estados de conexión y ubicación actual
    val isConnected by locationManager.isConnected.collectAsState()
    val currentLocation by locationManager.currentLocation.collectAsState()

    // Inicializa el cliente de ubicación
    LaunchedEffect(Unit) {
        locationManager.initLocationClient(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Botón para solicitar permisos
        if (!locationPermission.status.isGranted) {
            Button(onClick = { locationPermission.launchPermissionRequest() }) {
                Text("Solicitar Permiso de Ubicación")
            }
        }

        // Botones de conexión/desconexión
        if (locationPermission.status.isGranted) {
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (!isConnected) {
                            locationManager.connectDriver(context)
                        } else {
                            locationManager.disconnectDriver()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isConnected) "Desconectar" else "Conectar")
            }

            // Muestra la ubicación actual
            currentLocation?.let { location ->
                Text(
                    "Ubicación Actual: ${location.latitude}, ${location.longitude}",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}







