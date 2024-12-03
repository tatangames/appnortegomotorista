package com.alcaldiasantaananorte.nortegomotorista.permisos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

@Composable
fun SolicitarPermisos(
    onPermisosConcedidos: () -> Unit,
    onPermisosDenegados: () -> Unit
) {
    val context = LocalContext.current
    val permisoUbicacion = Manifest.permission.ACCESS_FINE_LOCATION
    val permisoNotificaciones = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else {
        TODO("VERSION.SDK_INT < TIRAMISU")
    }

    // Manejo de resultados de los permisos
    val solicitudPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisosConcedidos ->
        val ubicacionConcedido = permisosConcedidos[permisoUbicacion] ?: false
        val notificacionesConcedido = permisosConcedidos[permisoNotificaciones] ?: false

        if (ubicacionConcedido && notificacionesConcedido) {
            onPermisosConcedidos()
        } else {
            onPermisosDenegados()
        }
    }

    // Verificar si los permisos ya están concedidos
    val permisosConcedidosUbicacion = ContextCompat.checkSelfPermission(
        context, permisoUbicacion
    ) == PackageManager.PERMISSION_GRANTED

    val permisosConcedidosNotificaciones = ContextCompat.checkSelfPermission(
        context, permisoNotificaciones
    ) == PackageManager.PERMISSION_GRANTED

    // Si los permisos aún no están concedidos, se solicitan
    if (!permisosConcedidosUbicacion || !permisosConcedidosNotificaciones) {
        LaunchedEffect(Unit) {
            solicitudPermisos.launch(arrayOf(permisoUbicacion, permisoNotificaciones))
        }
    } else {
        onPermisosConcedidos()
    }
}


















