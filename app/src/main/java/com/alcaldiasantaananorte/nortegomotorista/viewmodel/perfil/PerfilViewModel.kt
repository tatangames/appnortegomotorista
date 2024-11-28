package com.alcaldiasantaananorte.nortegomotorista.viewmodel.perfil


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcaldiasantaananorte.nortegojetpackcompose.network.RetrofitBuilder
import com.alcaldiasantaananorte.nortegomotorista.model.datos.Event
import com.alcaldiasantaananorte.nortegomotorista.model.datos.ModeloBasico
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class PerfilViewModel : ViewModel() {

    private val _resultado = MutableLiveData<Event<ModeloBasico>>()
    val resultado: LiveData<Event<ModeloBasico>> get() = _resultado

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var disposable: Disposable? = null
    private var isRequestInProgress = false


    fun permisoMotorista(telefono: String) {

        // Verificar si ya hay una solicitud en progreso
        if (isRequestInProgress) return

        isRequestInProgress = true
        _isLoading.value = true

        disposable = RetrofitBuilder.getApiService().infoMotorista(telefono)
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


