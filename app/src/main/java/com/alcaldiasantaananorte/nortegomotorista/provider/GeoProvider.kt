package com.alcaldiasantaananorte.nortegomotorista.provider

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.GeoFirestore
import java.text.SimpleDateFormat
import java.util.Date

class GeoProvider {

    val collection = FirebaseFirestore.getInstance().collection("Locations")
    val geoFirestore = GeoFirestore(collection)


    @SuppressLint("SimpleDateFormat")
    fun saveLocation(idDriver: String, position: LatLng){
        geoFirestore.setLocation(idDriver, GeoPoint(position.latitude, position.longitude))
        val sdf = SimpleDateFormat("'Date\n'dd-MM-yyyy '\n\nand\n\nTime\n'HH:mm:ss z")
        val currentDateAndTime = sdf.format(Date())

        // Save additional driver name in Firestore
        FirebaseFirestore.getInstance()
            .collection("Locations")
            .document(idDriver)
            .set(mapOf("fecha" to currentDateAndTime), SetOptions.merge())
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