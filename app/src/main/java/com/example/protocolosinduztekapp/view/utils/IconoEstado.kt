package com.example.protocolosinduztekapp.view.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


@Composable
fun IconoEstado(valor: String, error: String?) {
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

