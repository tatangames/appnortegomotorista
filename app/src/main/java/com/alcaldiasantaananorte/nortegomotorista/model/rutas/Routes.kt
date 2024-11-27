package com.alcaldiasantaananorte.nortegomotorista.model.rutas

sealed class Routes(val route: String){
    object VistaSplash: Routes("splash")
    object VistaLogin: Routes("login")

    object VistaRegistro: Routes("registro/{telefono}/{identificador}") {
        fun createRoute(telefono: String, identificador: String) = "registro/$telefono/$identificador"
    }


    object VistaVerificarNumero: Routes("verificarNumero/{identificador}") {
        fun createRoute(identificador: String) = "verificarNumero/$identificador"
    }

    object VistaPrincipal: Routes("principal")


}