package com.example.protocolosinduztekapp.view.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.protocolosinduztekapp.navegations.Routes
import com.example.protocolosinduztekapp.repository.models.EquipoEntity
import com.example.protocolosinduztekapp.repository.models.PruebaEntity
import com.example.protocolosinduztekapp.viewmodels.Comparacion
import com.example.protocolosinduztekapp.viewmodels.EquipoViewModel
import java.util.Locale
import kotlin.math.abs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEquipoScreen(navController: NavController, viewModel: EquipoViewModel, equipoId: Long) {
    val estado by viewModel.detalle.collectAsState()


    LaunchedEffect(equipoId) { viewModel.cargarDetalle(equipoId) }

    val equipo = estado.equipo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(equipo?.codigo ?: "Equipo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Routes.formularioConEquipo(equipoId)) },
                icon = { Icon(Icons.Default.AddTask, contentDescription = null) },
                text = { Text("Nueva prueba") }
            )
        }
    ) { padding ->
        if (equipo == null || equipo.id != equipoId) {

            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { CabeceraEquipo(equipo = equipo, totalPruebas = estado.pruebas.size) }
                item { TarjetaComparacion(comparacion = estado.comparacion, totalPruebas = estado.pruebas.size) }
                item { Text("Historial de pruebas", style = MaterialTheme.typography.titleMedium) }

                if (estado.pruebas.isEmpty()) {
                    item {
                        Text(
                            "Este equipo aún no tiene pruebas registradas",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                items(estado.pruebas, key = { it.id }) { prueba ->
                    TarjetaPrueba(
                        prueba = prueba,
                        onVerProtocolo = { navController.navigate(Routes.protocolo(prueba.id)) },
                        modifier = Modifier.animateItem()
                    )
                }


                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }
}


@Composable
private fun CabeceraEquipo(equipo: EquipoEntity, totalPruebas: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (equipo.fotoPlacaUri != null) {
                AsyncImage(
                    model = equipo.fotoPlacaUri,
                    contentDescription = "Foto de la placa",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp))
                )
            } else {
                Icon(
                    Icons.Default.ElectricalServices,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp).padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(equipo.tipo, style = MaterialTheme.typography.titleMedium)
                Text("Marca: ${equipo.marca}", style = MaterialTheme.typography.bodyMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(equipo.instalacion, style = MaterialTheme.typography.bodyMedium)
                }
                Text("$totalPruebas pruebas registradas", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}


@Composable
private fun TarjetaComparacion(comparacion: Comparacion?, totalPruebas: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Comparación con la prueba anterior", style = MaterialTheme.typography.titleSmall)

            if (comparacion == null) {
                Text(
                    if (totalPruebas < 2) "Se necesitan al menos 2 pruebas para comparar"
                    else "Las mediciones no se pueden comparar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                val dif = comparacion.diferenciaPorcentaje
                val icono = when {
                    dif > 0 -> Icons.Default.ArrowUpward
                    dif < 0 -> Icons.Default.ArrowDownward
                    else -> Icons.Default.Remove
                }
                val colorEstado = if (comparacion.requiereRevision) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(formatear(comparacion.actual), style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(icono, contentDescription = null, tint = colorEstado, modifier = Modifier.size(18.dp))
                    Text(
                        String.format(Locale.getDefault(), "%.1f %% vs anterior", abs(dif)),
                        color = colorEstado,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Text("Anterior: ${formatear(comparacion.anterior)}", style = MaterialTheme.typography.bodySmall)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (comparacion.requiereRevision) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = colorEstado,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (comparacion.requiereRevision) "Variación mayor al 10 %: revisar el equipo"
                        else "Variación dentro de lo normal",
                        color = colorEstado,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// U
@Composable
private fun TarjetaPrueba(prueba: PruebaEntity, onVerProtocolo: () -> Unit, modifier: Modifier = Modifier) {
    var expandida by remember(prueba.id) { mutableStateOf(false) }

    Card(onClick = { expandida = !expandida }, modifier = modifier.fillMaxWidth()) {
        // ANIMACIÓN 5: la tarjeta cambia de tamaño suavemente al expandirse
        Column(modifier = Modifier.animateContentSize().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(prueba.fecha, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text("Medición: ${prueba.valorMedicion}", style = MaterialTheme.typography.titleSmall)
                Icon(
                    if (expandida) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expandida) "Contraer" else "Expandir"
                )
            }

            if (expandida) {
                Spacer(modifier = Modifier.height(8.dp))
                if (prueba.fotoUri != null) {
                    AsyncImage(
                        model = prueba.fotoUri,
                        contentDescription = "Foto de evidencia",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Text("Sin foto de evidencia", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (prueba.latitud != null && prueba.longitud != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            String.format(Locale.US, "%.4f, %.4f", prueba.latitud, prueba.longitud),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Text("Sin ubicación registrada", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                TextButton(onClick = onVerProtocolo) {
                    Icon(Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ver protocolo")
                }
            }
        }
    }
}


private fun formatear(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toLong().toString()
    else String.format(Locale.getDefault(), "%.2f", valor)