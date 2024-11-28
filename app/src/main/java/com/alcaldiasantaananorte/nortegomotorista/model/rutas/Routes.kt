package com.alcaldiasantaananorte.nortegomotorista.model.rutas

sealed class Routes(val route: String){
    object VistaSplash: Routes("splash")
    object VistaLogin: Routes("login")

    object VistaVerificarNumero: Routes("verificarNumero/{identificador}/{telefono}") {
        fun createRoute(identificador: String, telefono: String) = "verificarNumero/$identificador/$telefono"
    }

    object VistaPrincipal: Routes("principal")
    object VistaPerfil: Routes("perfil")

}