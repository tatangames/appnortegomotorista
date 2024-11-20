package com.alcaldiasantaananorte.nortegomotorista.pantallas.principal

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomModalCerrarSesion
import com.alcaldiasantaananorte.nortegomotorista.componentes.CustomToasty
import com.alcaldiasantaananorte.nortegomotorista.componentes.DrawerBody
import com.alcaldiasantaananorte.nortegomotorista.componentes.DrawerHeader
import com.alcaldiasantaananorte.nortegomotorista.componentes.ToastType
import com.alcaldiasantaananorte.nortegomotorista.componentes.itemsMenu
import com.alcaldiasantaananorte.nortegomotorista.model.rutas.Routes
import com.alcaldiasantaananorte.nortegomotorista.permisos.RiderDashboard
import com.alcaldiasantaananorte.nortegomotorista.permisos.RiderViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalScreen(
    navController: NavHostController,
) {
    val ctx = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var showModalCerrarSesion by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Crea el alcance de coroutine
    var imageUrls by remember { mutableStateOf(listOf<String>()) }
    var popNumeroBloqueado by remember { mutableStateOf(false) }

    val versionLocal = getVersionName(ctx)




    RiderDashboard()



    /* ModalNavigationDrawer(
         drawerState = drawerState,
         drawerContent = {
             ModalDrawerSheet {
                 DrawerHeader()
                 DrawerBody(items = itemsMenu) { item ->
                     when (item.id) {
                         1 -> {
                         }
                     }
                     scope.launch {
                         drawerState.close()
                     }
                 }
             }
         }
     ) {

         Scaffold(
             topBar = {
                 CenterAlignedTopAppBar(
                     title = {
                         Text(
                             text = "Servicios",
                             color = Color.White,
                             fontWeight = FontWeight.Medium
                         )
                     },
                     navigationIcon = {
                         IconButton(onClick = { scope.launch { drawerState.open() } }) {
                             Icon(Icons.Filled.Menu, contentDescription = null, tint = Color.White)
                         }
                     },
                     colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                         containerColor = Color.Gray,
                         titleContentColor = Color.White,
                         navigationIconContentColor = Color.White
                     )
                 )
             }
         ) { innerPadding ->








             if (showModalCerrarSesion) {
                 CustomModalCerrarSesion(showModalCerrarSesion,
                     "Cerrar Sesión",
                     onDismiss = { showModalCerrarSesion = false },
                     onAccept = {
                         scope.launch {

                             // cerrar modal
                             showModalCerrarSesion = false
                             navigateToLogin(navController)
                         }
                     })
             }





         }
     }*/
}

// redireccionar a vista login
private fun navigateToLogin(navController: NavHostController) {
    navController.navigate(Routes.VistaLogin.route) {
        popUpTo(Routes.VistaPrincipal.route) {
            inclusive = true // Elimina VistaPrincipal de la pila
        }
        launchSingleTop = true // Asegura que no se creen múltiples instancias de VistaLogin
    }
}




// Función auxiliar para obtener el versionName (puedes usarla fuera de composables)
fun getVersionName(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "N/A"
    } catch (e: PackageManager.NameNotFoundException) {
        "N/A"
    }
}



