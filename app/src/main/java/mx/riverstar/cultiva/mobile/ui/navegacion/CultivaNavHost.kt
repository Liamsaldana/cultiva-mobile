package mx.riverstar.cultiva.mobile.ui.navegacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.first
import mx.riverstar.cultiva.mobile.CultivaApp
import mx.riverstar.cultiva.mobile.ui.auth.LoginScreen
import mx.riverstar.cultiva.mobile.ui.auth.RegistroScreen
import mx.riverstar.cultiva.mobile.ui.metas.MetaFormScreen
import mx.riverstar.cultiva.mobile.ui.metas.MetasScreen

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val METAS = "metas"
    const val NUEVA_META = "metas/nueva"
    const val ARG_ID = "id"
    const val EDITAR_META = "metas/{$ARG_ID}"
    fun editarMeta(id: Long) = "metas/$id"
}

@Composable
fun CultivaNavHost() {
    val app = LocalContext.current.applicationContext as CultivaApp

    // Se decide UNA vez dónde arranca la app: con sesión guardada va directo a
    // las metas; sin sesión, al login.
    var inicio by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        if (inicio == null) {
            inicio = if (app.sesion.usuarioId.first() != null) Rutas.METAS else Rutas.LOGIN
        }
    }
    val destinoInicial = inicio
    if (destinoInicial == null) {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        return
    }

    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = destinoInicial) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                alEntrar = { nav.entrarALaApp() },
                alCrearCuenta = { nav.navigate(Rutas.REGISTRO) },
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                alRegistrar = { nav.entrarALaApp() },
                alVolver = { nav.popBackStack() },
            )
        }
        composable(Rutas.METAS) {
            MetasScreen(
                alCrear = { nav.navigate(Rutas.NUEVA_META) },
                alEditar = { id -> nav.navigate(Rutas.editarMeta(id)) },
                alCerrarSesion = { nav.salirAlLogin() },
            )
        }
        composable(Rutas.NUEVA_META) {
            MetaFormScreen(alTerminar = { nav.popBackStack() })
        }
        composable(
            Rutas.EDITAR_META,
            arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.LongType }),
        ) {
            MetaFormScreen(alTerminar = { nav.popBackStack() })
        }
    }
}

/**
 * Manejo de sesión en la pila de pantallas: al entrar se BORRAN de la pila el
 * Login y el Registro. Así, «Atrás» desde Mis metas cierra la app en lugar de
 * volver al Login.
 */
private fun NavHostController.entrarALaApp() {
    navigate(Rutas.METAS) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

/** Al cerrar sesión se vacía la pila: «Atrás» desde el Login no regresa a las metas. */
private fun NavHostController.salirAlLogin() {
    navigate(Rutas.LOGIN) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
