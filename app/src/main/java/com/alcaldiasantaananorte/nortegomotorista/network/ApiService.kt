package com.alcaldiasantaananorte.nortegojetpackcompose.network


import com.alcaldiasantaananorte.nortegomotorista.model.datos.ModeloBasico
import com.alcaldiasantaananorte.nortegomotorista.model.datos.ModeloListaTelefonos
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    // LISTA DE NUMEROS AUTORIZADOS PARA LOGIN FIREBASE
    @POST("app/verificar/motorista")
    @FormUrlEncoded
    fun modeloListaTelefonos(@Field("device") device: Int
                          ): Single<ModeloListaTelefonos>


    // INFORMACION MOTORISTA
    @POST("app/info/motorista")
    @FormUrlEncoded
    fun infoMotorista(@Field("telefono") telefono: String
    ): Single<ModeloBasico>






}


