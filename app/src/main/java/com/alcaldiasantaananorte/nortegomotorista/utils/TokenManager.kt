package com.alcaldiasantaananorte.nortegomotorista.utils


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


// Extension function to create a DataStore instance
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class TokenManager(private val context: Context) {

    // Clave para almacenar el token
    private val TEL_KEY = stringPreferencesKey("telefono")


    // Guardar token en DataStore
    suspend fun saveTelefono(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TEL_KEY] = token
        }
    }

    // Obtener token desde DataStore
    val telefonoToken: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[TEL_KEY] ?: ""
        }

    // Borrar token (Cierre de sesión)
    suspend fun deletePreferences() {
        context.dataStore.edit { preferences ->
            preferences.remove(TEL_KEY)
        }
    }
}