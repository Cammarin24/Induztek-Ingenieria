package com.example.protocolosinduztekapp.view.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource


fun tienePermisoUbicacion(context: Context): Boolean {
    val precisa = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val aproximada = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return precisa || aproximada
}


@SuppressLint("MissingPermission") // el permiso se revisa en la primera línea
fun obtenerUbicacionActual(context: Context, onResultado: (Double?, Double?) -> Unit) {
    if (!tienePermisoUbicacion(context)) {
        onResultado(null, null)
        return
    }
    val cliente = LocationServices.getFusedLocationProviderClient(context)
    cliente.getCurrentLocation(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        CancellationTokenSource().token
    )
        .addOnSuccessListener { ubicacion ->
            if (ubicacion != null) onResultado(ubicacion.latitude, ubicacion.longitude)
            else onResultado(null, null)
        }
        .addOnFailureListener { onResultado(null, null) }
}

