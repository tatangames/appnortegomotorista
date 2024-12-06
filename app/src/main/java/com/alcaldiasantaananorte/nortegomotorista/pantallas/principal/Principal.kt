package com.alcaldiasantaananorte.nortegomotorista.pantallas.principal

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
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
import com.alcaldiasantaananorte.nortegomotorista.permisos.SolicitarPermisos
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
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

object LocationState {
    var canSaveLocation: Boolean = true
}

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
    SolicitarPermisos(
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
                    actions = {
                        IconButton(onClick = { redireccionarAjustes(ctx) }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Configuración", tint = Color.White)
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


class LocationManager {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> get() = _isConnected

    fun setConnectionStatus(status: Boolean) {
        _isConnected.value = status
    }

    fun initLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationTrackingScreen(authProvider: AuthProvider) {

    val idauth =  authProvider.getId()
    val geoProvider = GeoProvider()
    val context = LocalContext.current
    val locationManager = remember { LocationManager() }

    // Estado de permisos de ubicación
    val locationPermission = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var isLoadingButton by remember { mutableStateOf(false) }

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

        if (locationPermission.status.isGranted) {
            // Verifica si el driver tiene ubicación activa en Firestore
            val exists = withContext(Dispatchers.IO) {
                geoProvider.checkLocationExists(idauth)
            }

            if (exists) {
                // Cambia el estado de conexión y arranca el servicio
                startLocationTrackingService()
                locationManager.setConnectionStatus(true)
            } else {
                locationManager.setConnectionStatus(false)
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
                    if (hasInternetConnection(context)) {
                        CoroutineScope(Dispatchers.IO).launch {
                            if (!isConnected) {
                                // Conectar: Inicia el servicio y cambia el estado
                                startLocationTrackingService()
                                locationManager.setConnectionStatus(true)
                                LocationState.canSaveLocation = true
                            } else {
                                isLoadingButton = true

                                val isLocationRemoved = geoProvider.removeLocationSuspend(idauth)
                                withContext(Dispatchers.Main) {
                                    isLoadingButton = false // Ocultar el indicador de carga
                                    if (isLocationRemoved) {
                                        // borro doc firebase, asi que bloquear rapido
                                        LocationState.canSaveLocation = false

                                        context.stopService(Intent(context, LocationTrackingService::class.java))
                                        locationManager.setConnectionStatus(false)
                                        Toast.makeText(context, "Desconectado exitosamente.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Error al desconectar. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }else{
                        Toast.makeText(context, "Sin conexión a internet.", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnected) Color.Red else ColorAzulGob
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isConnected) "Desconectar" else "Conectar",
                    fontSize = 18.sp
                )
            }

            // Indicador de carga
            if (isLoadingButton) {
                LoadingModal(isLoading = isLoadingButton)
            }

        }
    }
}


class LocationTrackingService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val geoProvider = GeoProvider()
    private lateinit var authProvider: String
    private lateinit var notificationManager: NotificationManager



    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Obtener el ID del usuario desde el intent
        authProvider = intent?.getStringExtra("USER_ID") ?: return START_NOT_STICKY
        startForeground(1, createNotification())
        // Crear una notificación para el servicio en primer plano
        createNotification()

        startLocationUpdates()

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación para Android Oreo y superior

        val channel = NotificationChannel(
            "LOCATION_SERVICE_CHANNEL",
            "Location Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)


        val notification = NotificationCompat.Builder(this, "LOCATION_SERVICE_CHANNEL")
            .setContentTitle("NORTEGO Ubicación")
            .setContentText("Seguimiento de ubicación activo")
            .setSmallIcon(R.drawable.camion)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Cambia a DEFAULT o HIGH
            .setOngoing(true) // Hace que la notificación sea persistente
            .build()

        startForeground(1, notification)

        return notification
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)

                    // Verificar conexión antes de enviar ubicación
                    if (hasInternetConnection(this@LocationTrackingService) && LocationState.canSaveLocation) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                geoProvider.saveLocation(authProvider, latLng)
                                Log.d("LocationService", "Ubicación enviada: $latLng")
                            } catch (e: Exception) {
                                Log.e("LocationService", "Error al enviar ubicación: ${e.localizedMessage}")
                            }
                        }

                    } else {
                        Log.w("LocationService", "Sin conexión. Ubicación no enviada.")
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
            val isLocationRemoved = geoProvider.removeLocationSuspend(authProvider)
            if (!isLocationRemoved) {
                Log.e("LocationTrackingService", "Failed to remove location on service destruction")
            }
        }
    }


    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, LocationTrackingService::class.java).apply {
            putExtra("USER_ID", authProvider)
            setPackage(packageName)
        }
        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext,
            1,
            restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmService = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000,
            restartServicePendingIntent
        )
        super.onTaskRemoved(rootIntent)
    }
}

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}