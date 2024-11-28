package com.alcaldiasantaananorte.nortegomotorista.provider

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class AuthProvider {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun cerrarSesion() {
        auth.signOut()
    }


    fun getId(): String {
        return auth.currentUser?.uid ?: ""
    }
}