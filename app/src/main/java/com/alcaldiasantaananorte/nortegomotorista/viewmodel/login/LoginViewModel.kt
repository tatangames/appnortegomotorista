package com.alcaldiasantaananorte.nortegomotorista.viewmodel.login

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import com.alcaldiasantaananorte.nortegomotorista.model.datos.Event
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import com.alcaldiasantaananorte.nortegomotorista.model.datos.ModeloListaTelefonos

class LoginViewModel : ViewModel() {

    private val _resultado = MutableLiveData<Event<ModeloListaTelefonos>>()
    val resultado: LiveData<Event<ModeloListaTelefonos>> get() = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false


    fun listaTelefonosAutorizadosRX() {

        // Verificar si ya hay una solicitud en progreso
        if (isRequestInProgress) return

        isRequestInProgress = true
        _isLoading.value = true

        // EL DEVICE IDENTIFICA QUE ESTOY MANDANDO SOLICITUD DESDE ANDROID
        disposable = RetrofitBuilder.getApiService().modeloListaTelefonos(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _isLoading.value = false
                    _resultado.value = Event(response)
                    isRequestInProgress = false
                },
                { error ->
                    _isLoading.value = false
                    isRequestInProgress = false
                }
            )
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose() // Limpiar la suscripción
    }


}


