package com.alcaldiasantaananorte.nortegomotorista.pantallas.principal

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
                            stringResource(R.string.ubicacion),
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


                        LocationTrackingScreen(authProvider)


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


class LocationManager(private val authProvider: String) {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)

    private val geoProvider = GeoProvider()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    // Add a method to check Firebase connection status
    suspend fun checkConnectionStatus() {
        try {
            // Check if a location document exists for this driver in Firebase
            val locationExists = geoProvider.checkLocationExists(authProvider)
            _isConnected.value = locationExists
        } catch (e: Exception) {
            // Handle any errors in checking connection status
            Log.e("LocationManager", "Error checking connection status", e)
            _isConnected.value = false
        }
    }

    fun initLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun connectDriver(context: Context) {
        try {
            // If already connected, do nothing
            if (isConnected.value) return

            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val latLng = LatLng(location.latitude, location.longitude)

                        _currentLocation.value = latLng

                        CoroutineScope(Dispatchers.IO).launch {
                            geoProvider.saveLocation(authProvider, latLng)
                        }
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )

            _isConnected.value = true
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun disconnectDriver() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }

        geoProvider.removeLocation(authProvider)

        _isConnected.value = false
        _currentLocation.value = null
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationTrackingScreen(authProvider: AuthProvider) {

    val idauth =  authProvider.getId()

    val context = LocalContext.current
    val locationManager = remember { LocationManager(idauth) }

    // Estado de permisos de ubicación
    val locationPermission = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Estados de conexión y ubicación actual
    val isConnected by locationManager.isConnected.collectAsState()

    // Función para iniciar el servicio de seguimiento
    fun startLocationTrackingService() {
        val serviceIntent = Intent(context, LocationTrackingService::class.java).apply {
            putExtra("USER_ID", authProvider.getId())
        }

        // Para Android 8.0 (Oreo) y superior
        context.startForegroundService(serviceIntent)
    }

    // Inicializa el cliente de ubicación
    // Check connection status when the screen is first loaded
    LaunchedEffect(Unit) {
        locationManager.initLocationClient(context)

        // Check if already connected in Firebase
        if (locationPermission.status.isGranted) {
            locationManager.checkConnectionStatus()

            if(isConnected){
                startLocationTrackingService()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (!locationPermission.status.isGranted) {
            Button(onClick = { locationPermission.launchPermissionRequest() }) {
                Text("Solicitar Permiso de Ubicación")
            }
        }

        if (locationPermission.status.isGranted) {
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (!isConnected) {
                            startLocationTrackingService()
                            locationManager.connectDriver(context)
                        } else {
                            context.stopService(Intent(context, LocationTrackingService::class.java))
                            locationManager.disconnectDriver()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnected) Color.Red else ColorAzulGob
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isConnected) "Desconectar" else "Conectar",
                    fontSize = 18.sp // Cambia el tamaño de la fuente aquí
                )
            }
        }
    }
}


class LocationTrackingService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val geoProvider = GeoProvider()
    private lateinit var authProvider: String

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Obtener el ID del usuario desde el intent
        authProvider = intent?.getStringExtra("USER_ID") ?: return START_NOT_STICKY

        // Crear una notificación para el servicio en primer plano
        createNotification()

        startLocationUpdates()

        return START_STICKY
    }

    private fun createNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación para Android Oreo y superior

        val channel = NotificationChannel(
            "LOCATION_SERVICE_CHANNEL",
            "Location Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)


        val notification = NotificationCompat.Builder(this, "LOCATION_SERVICE_CHANNEL")
            .setContentTitle("Tracking de Ubicación")
            .setContentText("Seguimiento de ubicación activo")
            .setSmallIcon(R.drawable.alerta) // Reemplaza con tu ícono
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)

                    // Guardar ubicación en Firebase en un scope de IO
                    CoroutineScope(Dispatchers.IO).launch {
                        geoProvider.saveLocation(authProvider, latLng)
                    }
                }
            }
        }

        // Verificar permisos antes de solicitar actualizaciones
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // Opcional: remover la ubicación de Firebase cuando el servicio se detiene
        CoroutineScope(Dispatchers.IO).launch {
            geoProvider.removeLocation(authProvider)
        }
    }
}

