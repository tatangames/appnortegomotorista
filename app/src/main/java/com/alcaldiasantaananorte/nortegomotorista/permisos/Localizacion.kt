package com.alcaldiasantaananorte.nortegomotorista.permisos

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine



class RiderViewModel(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    var currentLocation by mutableStateOf(LatLng(0.0, 0.0))
    var isOnline by mutableStateOf(false)
    var availableRides by mutableStateOf(listOf<Ride>())

    suspend fun updateLocation(location: LatLng) {
        val userId = auth.currentUser?.uid ?: return

        // Actualizar ubicación en Firestore
        firestore.collection("riders")
            .document(userId)
            .update(mapOf(
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "isOnline" to isOnline
            ))
    }

    suspend fun fetchAvailableRides() {
        val ridesSnapshot = firestore.collection("rides")
            .whereEqualTo("status", "pending")
            .get().await()

        availableRides = ridesSnapshot.toObjects(Ride::class.java)
    }

    // Método para simular ubicación
    fun simulateLocation() {
        // Ejemplo de simulación en Ciudad de México
        val locations = listOf(
            LatLng(19.4326, -99.1332),  // Centro CDMX
            LatLng(19.4361, -99.1675),  // Zona Santa Fe
            LatLng(19.4500, -99.1200)   // Zona del Ángel
        )

        currentLocation = locations.random()
    }
}

data class Ride(
    val id: String = "",
    val origin: LatLng = LatLng(0.0, 0.0),
    val destination: LatLng = LatLng(0.0, 0.0),
    val status: String = "pending"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderDashboard() {
    val context = LocalContext.current
    val viewModel = remember { RiderViewModel(context) }

    var showSimulationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rider Dashboard") },
                actions = {
                    Switch(
                        checked = viewModel.isOnline,
                        onCheckedChange = {
                            viewModel.isOnline = it
                            // Lógica adicional de cambio de estado
                        }
                    )
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(0.7f),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        viewModel.currentLocation, 15f
                    )
                }
            ) {
                Marker(
                    state = MarkerState(position = viewModel.currentLocation),
                    title = "Mi Ubicación"
                )
            }

            Button(onClick = { showSimulationDialog = true }) {
                Text("Simular Ubicación")
            }

            if (showSimulationDialog) {
                AlertDialog(
                    onDismissRequest = { showSimulationDialog = false },
                    title = { Text("Simular Ubicación") },
                    text = { Text("¿Deseas simular una nueva ubicación?") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.simulateLocation()
                            showSimulationDialog = false
                        }) {
                            Text("Simular")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSimulationDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}


















