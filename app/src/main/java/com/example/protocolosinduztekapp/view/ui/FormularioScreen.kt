package com.example.protocolosinduztekapp.view.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.protocolosinduztekapp.view.utils.AnimacionEntradaFormulario
import com.example.protocolosinduztekapp.view.utils.CameraCapture
import com.example.protocolosinduztekapp.view.utils.PhotoBox
import com.example.protocolosinduztekapp.view.utils.obtenerUbicacionActual
import com.example.protocolosinduztekapp.view.utils.tienePermisoUbicacion
import com.example.protocolosinduztekapp.viewmodels.PruebaViewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(navController: NavController, viewModel: PruebaViewModel) {
    val context = LocalContext.current
    val estado by viewModel.formulario.collectAsState()
    val equipos by viewModel.equipos.collectAsState()


    var menuAbierto by remember { mutableStateOf(false) }


    var mostrarCamara by rememberSaveable { mutableStateOf(false) }


    var mostrarDialogoUbicacion by remember { mutableStateOf(false) }


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

    // --- RECURSO NATIVO 2: ubicación ---
    val obtenerUbicacion: () -> Unit = {
        viewModel.onUbicacionCargando()
        obtenerUbicacionActual(context) { latitud, longitud ->
            if (latitud != null && longitud != null) {
                viewModel.onUbicacionObtenida(latitud, longitud)
            } else {
                viewModel.onUbicacionError("No se pudo obtener la ubicación. Revisa que el GPS esté activado.")
            }
        }
    }


    val permisoUbicacionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) obtenerUbicacion() else mostrarDialogoUbicacion = true
    }

    val pedirUbicacion: () -> Unit = {
        if (tienePermisoUbicacion(context)) {
            obtenerUbicacion()
        } else {
            permisoUbicacionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }


    if (mostrarCamara) {
        CameraCapture(
            carpeta = "pruebas",
            onImageCaptured = { uri ->
                viewModel.onFotoTomada(uri.toString())
                mostrarCamara = false
            },
            onClose = { mostrarCamara = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Protocolo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                    .verticalScroll(rememberScrollState())
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
                        leadingIcon = { Icon(Icons.Default.ElectricalServices, contentDescription = null) },
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
                    leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null) },
                    trailingIcon = { IconoEstado(valor = estado.medicion, error = estado.errorMedicion) },
                    isError = estado.errorMedicion != null,
                    supportingText = { estado.errorMedicion?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )


                Text("Foto de evidencia (opcional)", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PhotoBox(
                        imageUri = estado.fotoUri,
                        circle = false,
                        onTakePhoto = { mostrarCamara = true }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))


                Text("Ubicación de la prueba (opcional)", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))
                when {
                    estado.cargandoUbicacion -> Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Obteniendo ubicación…")
                    }
                    estado.latitud != null && estado.longitud != null -> AssistChip(
                        onClick = pedirUbicacion, // al tocar, actualiza la ubicación
                        label = {
                            Text(String.format(Locale.US, "%.4f, %.4f", estado.latitud, estado.longitud))
                        },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                    )
                    else -> OutlinedButton(
                        onClick = pedirUbicacion,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar ubicación")
                    }
                }
                estado.errorUbicacion?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.guardarFormulario() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Guardar ")
                }
            }
        }
    }


    if (mostrarDialogoUbicacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoUbicacion = false },
            icon = { Icon(Icons.Default.LocationOff, contentDescription = null) },
            title = { Text("Ubicación no permitida") },
            text = {
                Text("Sin este permiso la prueba se guardará sin coordenadas. Registrar la ubicación ayuda a la trazabilidad del protocolo.")
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoUbicacion = false
                    permisoUbicacionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                }) { Text("Intentar de nuevo") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoUbicacion = false }) { Text("Continuar sin ubicación") }
            }
        )
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