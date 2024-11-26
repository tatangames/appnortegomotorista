package com.alcaldiasantaananorte.nortegomotorista.model.datos

import com.google.gson.annotations.SerializedName

data class ModeloListaTelefonos(
    @SerializedName("success") val success: Int,
    @SerializedName("telefono") val telefono: List<Telefono>,
)

data class Telefono(
    @SerializedName("id") val id: Int,
    @SerializedName("telefono") val telefono: String?,
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