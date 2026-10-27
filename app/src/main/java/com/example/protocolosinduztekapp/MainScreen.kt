package com.example.protocolosinduztekapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(viewModel: PruebaViewModel) {
    val navController = rememberNavController()
    val pruebas by viewModel.listaPruebas.collectAsState()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(navController = navController, totalRegistros = pruebas.size)
        }
        composable("formulario") {
            FormularioScreen(navController = navController, viewModel = viewModel)
        }
        composable("historial") {
            HistorialScreen(navController = navController, viewModel = viewModel, pruebas = pruebas)
        }
    }
}

// --- 1. DASHBOARD ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, totalRegistros: Int) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Induztek Ingeniería - Terreno") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Gestión de Protocolos Eléctricos", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Registros en base de datos local: $totalRegistros", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("formulario") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar Nuevo Protocolo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("historial") },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.List, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Historial de Pruebas")
            }
        }
    }
}

// --- 2. FORMULARIO CON FECHA AUTOMÁTICA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(navController: NavController, viewModel: PruebaViewModel) {
    val context = LocalContext.current
    var codigo by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var medicion by remember { mutableStateOf("") }
    var errorValidacion by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Protocolo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código del Equipo") },
                isError = errorValidacion && codigo.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Tipo de Equipo (ej. Transformador)") },
                isError = errorValidacion && tipo.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = medicion,
                onValueChange = { medicion = it },
                label = { Text("Valor de Medición (V / A)") },
                isError = errorValidacion && medicion.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorValidacion) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("⚠️ Completa todos los campos obligatorios", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (codigo.isBlank() || tipo.isBlank() || medicion.isBlank()) {
                        errorValidacion = true
                    } else {
                        // Generar fecha y hora actual automáticamente (Ej: 27/10/2026 12:30)
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val fechaActual = dateFormat.format(Date())

                        viewModel.guardarPrueba(codigo, tipo, medicion, fechaActual)
                        Toast.makeText(context, "¡Protocolo guardado con éxito!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Guardar en Base de Datos")
            }
        }
    }
}

// --- 3. HISTORIAL MOSTRANDO LA FECHA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(navController: NavController, viewModel: PruebaViewModel, pruebas: List<PruebaEntity>) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Protocolos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (pruebas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay protocolos registrados en el sistema",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    items(pruebas) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Equipo: ${item.tipoEquipo}", style = MaterialTheme.typography.titleSmall)
                                    Text("Código: ${item.codigoEquipo} | Medición: ${item.valorMedicion}", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    // Muestra la fecha registrada automáticamente
                                    Text("🕒 ${item.fecha}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                }
                                IconButton(onClick = {
                                    viewModel.eliminarPrueba(item)
                                    Toast.makeText(context, "Protocolo eliminado", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}