package com.example.protocolosinduztekapp.navegations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.protocolosinduztekapp.view.ui.DashboardScreen
import com.example.protocolosinduztekapp.view.ui.FormularioScreen
import com.example.protocolosinduztekapp.view.ui.HistorialScreen
import com.example.protocolosinduztekapp.viewmodels.PruebaViewModel

// Rutas de navegación centralizadas
object Routes {
    const val DASHBOARD = "dashboard"
    const val FORMULARIO = "formulario"
    const val HISTORIAL = "historial"
}

@Composable
fun AppNavigation(viewModel: PruebaViewModel) {
    val navController = rememberNavController()
    val pruebas by viewModel.listaPruebas.collectAsState()

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController = navController, totalRegistros = pruebas.size)
        }
        composable(Routes.FORMULARIO) {
            FormularioScreen(navController = navController, viewModel = viewModel)
        }
        composable(Routes.HISTORIAL) {
            HistorialScreen(navController = navController, viewModel = viewModel, pruebas = pruebas)
        }
    }
}

