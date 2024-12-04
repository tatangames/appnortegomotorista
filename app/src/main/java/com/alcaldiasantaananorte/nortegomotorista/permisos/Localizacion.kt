package com.alcaldiasantaananorte.nortegomotorista.permisos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


// PERMISOS DE UBICACION Y PERMITIR NOTIFICACION POST PARA MOSTRAR ALERTA SEGUNDO PLANO
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