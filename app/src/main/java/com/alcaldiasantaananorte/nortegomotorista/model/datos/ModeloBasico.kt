package com.alcaldiasantaananorte.nortegomotorista.model.datos

import com.google.gson.annotations.SerializedName

data class ModeloBasico(
    @SerializedName("success") val success: Int,
    @SerializedName("cambios") val cambios: Int,
    @SerializedName("registrado") val registrado: Int
)

data class ModeloListaTelefonos(
    @SerializedName("success") val success: Int,
    @SerializedName("lista") val listado: List<Telefono>,
)

data class Telefono(
    @SerializedName("id") val id: Int,
    @SerializedName("numero") val telefono: String,
    @SerializedName("registrado") val registrado: Int,
)


// UTILIZADO PARA EJECUTAR 1 VEZ LAS PETICIONES RETROFIT
class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}