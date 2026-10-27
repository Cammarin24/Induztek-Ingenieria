package com.example.protocolosinduztekapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_pruebas")
data class PruebaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val codigoEquipo: String,
    val tipoEquipo: String,
    val valorMedicion: String,
    val fecha: String // <-- Nuevo campo para la fecha y hora
)