package com.alcaldiasantaananorte.nortegomotorista.model.rutas

sealed class Routes(val route: String){
    object VistaSplash: Routes("splash")
    object VistaLogin: Routes("login")
}