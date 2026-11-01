package com.example.protocolosinduztekapp.view.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.protocolosinduztekapp.view.utils.AnimacionEntradaFormulario
import com.example.protocolosinduztekapp.viewmodels.PruebaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(navController: NavController, viewModel: PruebaViewModel) {
    val context = LocalContext.current
    val estado by viewModel.formulario.collectAsState()
    val equipos by viewModel.equipos.collectAsState()


    var menuAbierto by remember { mutableStateOf(false) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }


    DisposableEffect(Unit) {
        onDispose { viewModel.limpiarFormulario() }
    }


    LaunchedEffect(estado.guardado) {
        if (estado.guardado) {
            Toast.makeText(context, "¡Protocolo guardado con éxito!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

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

        AnimacionEntradaFormulario(visible = visible) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                ExposedDropdownMenuBox(
                    expanded = menuAbierto,
                    onExpandedChange = { menuAbierto = it }
                ) {
                    OutlinedTextField(
                        value = estado.equipoTexto,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Equipo") },
                        placeholder = { Text("Selecciona un equipo") },
                        leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto) },
                        isError = estado.errorEquipo != null,
                        supportingText = { estado.errorEquipo?.let { Text(it) } },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        equipos.forEach { equipo ->
                            DropdownMenuItem(
                                text = { Text("${equipo.codigo} · ${equipo.tipo}") },
                                onClick = {
                                    viewModel.onEquipoSeleccionado(equipo)
                                    menuAbierto = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = estado.medicion,
                    onValueChange = viewModel::onMedicionChange,
                    label = { Text("Valor de Medición (V / A)") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    trailingIcon = { IconoEstado(valor = estado.medicion, error = estado.errorMedicion) },
                    isError = estado.errorMedicion != null,
                    supportingText = { estado.errorMedicion?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.guardarFormulario() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Guardar en Base de Datos")
                }
            }
        }
    }
}


@Composable
private fun IconoEstado(valor: String, error: String?) {
    when {
        error != null -> Icon(
            Icons.Default.Warning,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error
        )
        valor.isNotBlank() -> Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Correcto",
            tint = Color(0xFF2E7D32)
        )
    }
}