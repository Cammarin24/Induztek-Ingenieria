package com.example.protocolosinduztekapp.view.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.protocolosinduztekapp.view.utils.AnimacionEntradaFormulario
import com.example.protocolosinduztekapp.view.utils.CameraCapture
import com.example.protocolosinduztekapp.view.utils.IconoEstado
import com.example.protocolosinduztekapp.view.utils.PhotoBox
import com.example.protocolosinduztekapp.viewmodels.EquipoViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoEquipoScreen(navController: NavController, viewModel: EquipoViewModel) {
    val context = LocalContext.current
    val estado by viewModel.formulario.collectAsState()

    var menuTipoAbierto by remember { mutableStateOf(false) }
    var mostrarCamara by rememberSaveable { mutableStateOf(false) }


    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }


    DisposableEffect(Unit) {
        onDispose { viewModel.limpiarFormulario() }
    }


    LaunchedEffect(estado.guardado) {
        if (estado.guardado) {
            Toast.makeText(context, "Equipo registrado", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }


    if (mostrarCamara) {
        CameraCapture(
            carpeta = "equipos",
            onImageCaptured = { uri ->
                viewModel.onFotoPlacaTomada(uri.toString())
                mostrarCamara = false
            },
            onClose = { mostrarCamara = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Equipo") },
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

                OutlinedTextField(
                    value = estado.codigo,
                    onValueChange = viewModel::onCodigoChange,
                    label = { Text("Código del equipo") },
                    placeholder = { Text("TR-003") },
                    leadingIcon = { Icon(Icons.Default.QrCode2, contentDescription = null) },
                    trailingIcon = { IconoEstado(valor = estado.codigo, error = estado.errorCodigo) },
                    isError = estado.errorCodigo != null,
                    supportingText = { estado.errorCodigo?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )


                ExposedDropdownMenuBox(
                    expanded = menuTipoAbierto,
                    onExpandedChange = { menuTipoAbierto = it }
                ) {
                    OutlinedTextField(
                        value = estado.tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de equipo") },
                        placeholder = { Text("Selecciona el tipo") },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTipoAbierto) },
                        isError = estado.errorTipo != null,
                        supportingText = { estado.errorTipo?.let { Text(it) } },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = menuTipoAbierto,
                        onDismissRequest = { menuTipoAbierto = false }
                    ) {
                        viewModel.tiposEquipo.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    viewModel.onTipoSeleccionado(tipo)
                                    menuTipoAbierto = false
                                }
                            )
                        }
                    }
                }

                // Marca
                OutlinedTextField(
                    value = estado.marca,
                    onValueChange = viewModel::onMarcaChange,
                    label = { Text("Marca") },
                    placeholder = { Text("Ej. Schneider") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    trailingIcon = { IconoEstado(valor = estado.marca, error = estado.errorMarca) },
                    isError = estado.errorMarca != null,
                    supportingText = { estado.errorMarca?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = estado.instalacion,
                    onValueChange = viewModel::onInstalacionChange,
                    label = { Text("Instalación") },
                    placeholder = { Text("Ej. S/E Norte") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                    trailingIcon = { IconoEstado(valor = estado.instalacion, error = estado.errorInstalacion) },
                    isError = estado.errorInstalacion != null,
                    supportingText = { estado.errorInstalacion?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )


                Text("Foto de la placa (opcional)", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PhotoBox(
                        imageUri = estado.fotoPlacaUri,
                        circle = false,
                        onTakePhoto = { mostrarCamara = true }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = viewModel::guardarEquipo,
                    enabled = !estado.guardando,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (estado.guardando) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Guardar equipo")
                    }
                }
            }
        }
    }
}

