package com.alcaldiasantaananorte.nortegomotorista.provider

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.GeoFirestore

class GeoProvider {

    val collection = FirebaseFirestore.getInstance().collection("Locations")
    val geoFirestore = GeoFirestore(collection)

    fun saveLocation(idDriver: String, position: LatLng){
        geoFirestore.setLocation(idDriver, GeoPoint(position.latitude, position.longitude))
    }

    suspend fun removeLocationSuspend(driverId: String): Boolean {
        return try {
            collection.document(driverId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("GeoProvider", "Error removing location: ${e.localizedMessage}")
            false
        }
    }

    suspend fun checkLocationExists(driverId: String): Boolean {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("Locations")
                .document(driverId)
                .get()
                .await()

            snapshot.exists()
        } catch (e: Exception) {
            Log.e("GeoProvider", "Error checking location existence", e)
            false
        }
    }

}