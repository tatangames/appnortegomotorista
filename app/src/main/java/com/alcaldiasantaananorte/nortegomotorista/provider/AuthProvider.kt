package com.alcaldiasantaananorte.nortegomotorista.provider

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class AuthProvider {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun cerrarSesion() {
        auth.signOut()
    }
}