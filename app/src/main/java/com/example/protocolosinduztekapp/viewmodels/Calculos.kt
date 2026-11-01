package com.example.protocolosinduztekapp.viewmodels

import kotlin.math.abs


fun compararMediciones(actual: String, anterior: String): Comparacion? {
    val valorActual = actual.replace(',', '.').toDoubleOrNull() ?: return null
    val valorAnterior = anterior.replace(',', '.').toDoubleOrNull() ?: return null
    if (valorAnterior == 0.0) return null
    val diferencia = (valorActual - valorAnterior) / valorAnterior * 100
    return Comparacion(
        actual = valorActual,
        anterior = valorAnterior,
        diferenciaPorcentaje = diferencia,
        requiereRevision = abs(diferencia) > 10
    )
}

