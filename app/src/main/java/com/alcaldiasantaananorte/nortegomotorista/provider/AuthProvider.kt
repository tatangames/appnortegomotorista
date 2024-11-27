package com.alcaldiasantaananorte.nortegomotorista.provider

import android.app.Activity
import com.alcaldiasantaananorte.nortegomotorista.model.datos.Driver
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class AuthProvider {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var verificationId: String? = null

    // Iniciar verificación de número de teléfono
    fun iniciarVerificacionTelefono(
        telefono: String,
        activity: Activity,
        onCodeSent: (String) -> Unit,
        onVerificationCompleted: (AuthCredential) -> Unit,
        onError: (String) -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(telefono) // Número de teléfono
            .setTimeout(60L, TimeUnit.SECONDS) // Tiempo de espera para el código
            .setActivity(activity) // Actividad para redirigir los callbacks
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Verificación automática completada
                    onVerificationCompleted(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Error en la verificación
                    onError(e.message ?: "Error desconocido")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Código enviado al teléfono
                    this@AuthProvider.verificationId = verificationId
                    onCodeSent(verificationId)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Confirmar código recibido
    fun verificarCodigo(
        codigo: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId ?: "", codigo)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onError("Usuario no encontrado")
                    }
                } else {
                    onError(task.exception?.message ?: "Error de autenticación")
                }
            }
    }

    // Registrar un Driver en Firestore
    fun registrarDriver(driver: Driver, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Drivers").document(userId)
                .set(driver)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
        } else {
            onComplete(false)
        }
    }
}