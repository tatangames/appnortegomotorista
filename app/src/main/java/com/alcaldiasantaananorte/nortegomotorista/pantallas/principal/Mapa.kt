package com.alcaldiasantaananorte.nortegomotorista.pantallas.mapa


import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Comienza el servicio en primer plano con la notificación inmediatamente
        startForeground(1, createNotification())

        // Aquí puedes hacer otras operaciones, pero la notificación debe mostrarse rápidamente
        return START_STICKY
    }


    private fun createNotification(): Notification {
        val channelId = "location_service_channel"
        val channelName = "Location Service"
        val notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Ubicación en tiempo real")
            .setContentText("Compartiendo tu ubicación en tiempo real")
            .setSmallIcon(R.drawable.alerta)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}


fun isServiceRunning(context: Context, serviceClass: Class<out Service>): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}




