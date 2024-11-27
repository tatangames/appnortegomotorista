package com.alcaldiasantaananorte.nortegomotorista.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.ui.graphics.vector.ImageVector
import com.alcaldiasantaananorte.nortegomotorista.R
import androidx.compose.material.icons.filled.Person

sealed class ItemsMenuLateral(
    val icon: ImageVector,
    val idString: Int,
    val id: Int
) {
    object ItemMenu1 : ItemsMenuLateral(
        Icons.Filled.Person,
        R.string.perfil,
        1
    )

    object ItemMenu2 : ItemsMenuLateral(
        Icons.AutoMirrored.Filled.Logout,
        R.string.cerrar_sesion,
        2
    )
}

// Lista de items del menú lateral
val itemsMenu = listOf(ItemsMenuLateral.ItemMenu1, ItemsMenuLateral.ItemMenu2)
