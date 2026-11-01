package com.example.protocolosinduztekapp.repository.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "tabla_equipos",
    indices = [Index(value = ["codigo"], unique = true)]
)
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val codigo: String,
    val tipo: String,
    val marca: String,
    val instalacion: String,
    val fotoPlacaUri: String? = null
)

