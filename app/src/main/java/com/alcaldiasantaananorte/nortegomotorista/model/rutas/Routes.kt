package com.alcaldiasantaananorte.nortegomotorista.model.rutas

sealed class Routes(val route: String){
    object VistaSplash: Routes("splash")
    object VistaLogin: Routes("login")
    object VistaVerificarNumero: Routes("verificarNumero/{identificador}") {
        fun createRoute(identificador: String) = "verificarNumero/$identificador"
    }

    object VistaPrincipal: Routes("principal")


}