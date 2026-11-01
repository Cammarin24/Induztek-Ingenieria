package com.example.protocolosinduztekapp.navegations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.protocolosinduztekapp.view.ui.DashboardScreen
import com.example.protocolosinduztekapp.view.ui.DetalleEquipoScreen
import com.example.protocolosinduztekapp.view.ui.EquiposScreen
import com.example.protocolosinduztekapp.view.ui.FormularioScreen
import com.example.protocolosinduztekapp.view.ui.HistorialScreen
import com.example.protocolosinduztekapp.view.ui.NuevoEquipoScreen
import com.example.protocolosinduztekapp.view.ui.ProtocoloScreen
import com.example.protocolosinduztekapp.view.utils.navEnterTransition
import com.example.protocolosinduztekapp.view.utils.navExitTransition
import com.example.protocolosinduztekapp.view.utils.navPopEnterTransition
import com.example.protocolosinduztekapp.view.utils.navPopExitTransition
import com.example.protocolosinduztekapp.viewmodels.EquipoViewModel
import com.example.protocolosinduztekapp.viewmodels.PruebaViewModel


object Routes {
    const val DASHBOARD = "dashboard"
    const val FORMULARIO = "formulario"
    const val FORMULARIO_CON_EQUIPO = "formulario?equipoId={equipoId}"
    const val HISTORIAL = "historial"
    const val EQUIPOS = "equipos"
    const val NUEVO_EQUIPO = "nuevo_equipo"
    const val DETALLE_EQUIPO = "detalle_equipo/{equipoId}"
    const val PROTOCOLO = "protocolo/{pruebaId}"

    fun formularioConEquipo(equipoId: Long) = "formulario?equipoId=$equipoId"
    fun detalleEquipo(equipoId: Long) = "detalle_equipo/$equipoId"
    fun protocolo(pruebaId: Long) = "protocolo/$pruebaId"
}

@Composable
fun AppNavigation(viewModel: PruebaViewModel) {
    val navController = rememberNavController()
    val pruebas by viewModel.listaPruebas.collectAsState()


    val equipoViewModel: EquipoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD,

        enterTransition = navEnterTransition,
        exitTransition = navExitTransition,
        popEnterTransition = navPopEnterTransition,
        popExitTransition = navPopExitTransition
    ) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController = navController, totalRegistros = pruebas.size)
        }

        composable(
            route = Routes.FORMULARIO_CON_EQUIPO,
            arguments = listOf(navArgument("equipoId") { type = NavType.LongType; defaultValue = 0L })
        ) { entry ->
            FormularioScreen(
                navController = navController,
                viewModel = viewModel,
                equipoIdInicial = entry.arguments?.getLong("equipoId") ?: 0L
            )
        }
        composable(Routes.HISTORIAL) {
            HistorialScreen(navController = navController, viewModel = viewModel, pruebas = pruebas)
        }
        composable(Routes.EQUIPOS) {
            EquiposScreen(navController = navController, viewModel = equipoViewModel)
        }
        composable(Routes.NUEVO_EQUIPO) {
            NuevoEquipoScreen(navController = navController, viewModel = equipoViewModel)
        }
        composable(
            route = Routes.DETALLE_EQUIPO,
            arguments = listOf(navArgument("equipoId") { type = NavType.LongType })
        ) { entry ->
            DetalleEquipoScreen(
                navController = navController,
                viewModel = equipoViewModel,
                equipoId = entry.arguments?.getLong("equipoId") ?: 0L
            )
        }
        composable(
            route = Routes.PROTOCOLO,
            arguments = listOf(navArgument("pruebaId") { type = NavType.LongType })
        ) { entry ->
            ProtocoloScreen(
                navController = navController,
                viewModel = viewModel,
                pruebaId = entry.arguments?.getLong("pruebaId") ?: 0L
            )
        }
    }
}