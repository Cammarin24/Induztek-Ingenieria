package com.example.protocolosinduztekapp.view.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.protocolosinduztekapp.viewmodels.PruebaViewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocoloScreen(navController: NavController, viewModel: PruebaViewModel, pruebaId: Long) {
    val estado by viewModel.protocolo.collectAsState()


    LaunchedEffect(pruebaId) { viewModel.cargarProtocolo(pruebaId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Protocolo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        val prueba = estado.prueba
        when {
            estado.cargando -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            prueba == null -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("No se encontró la prueba") }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("N° de protocolo", style = MaterialTheme.typography.labelMedium)
                                Text(estado.numero, style = MaterialTheme.typography.titleLarge)
                            }
                            ChipResultado(resultado = estado.resultado)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        FilaDato("Fecha", prueba.fecha)
                        FilaDato("Equipo", "${prueba.codigoEquipo} · ${prueba.tipoEquipo}")
                        estado.equipo?.let { equipo ->
                            FilaDato("Marca", equipo.marca)
                            FilaDato("Instalación", equipo.instalacion)
                        }
                    }
                }


                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Resultado de la medición", style = MaterialTheme.typography.titleSmall)
                        FilaDato("Medición actual", prueba.valorMedicion)
                        val anterior = estado.anterior
                        if (anterior == null) {
                            Text(
                                "Primera prueba registrada para este equipo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        } else {
                            FilaDato("Medición anterior (${anterior.fecha})", anterior.valorMedicion)
                            estado.comparacion?.let { comparacion ->
                                FilaDato(
                                    "Variación",
                                    String.format(Locale.getDefault(), "%+.1f %%", comparacion.diferenciaPorcentaje)
                                )
                            }
                        }
                    }
                }


                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Evidencia", style = MaterialTheme.typography.titleSmall)
                        if (prueba.fotoUri != null) {
                            AsyncImage(
                                model = prueba.fotoUri,
                                contentDescription = "Foto de evidencia",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            Text("Sin foto de evidencia", color = MaterialTheme.colorScheme.outline)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (prueba.latitud != null && prueba.longitud != null)
                                    String.format(Locale.US, "%.4f, %.4f", prueba.latitud, prueba.longitud)
                                else "Sin ubicación registrada"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
private fun ChipResultado(resultado: String) {
    val conforme = resultado == "CONFORME"
    val colorTexto = if (conforme) Color(0xFF1E7D3A) else MaterialTheme.colorScheme.error
    Surface(
        color = if (conforme) Color(0xFFD5F1DB) else MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (conforme) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = colorTexto,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(resultado, style = MaterialTheme.typography.labelLarge, color = colorTexto)
        }
    }
}

