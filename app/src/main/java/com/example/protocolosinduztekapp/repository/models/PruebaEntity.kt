package com.example.protocolosinduztekapp.repository.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "tabla_pruebas",
    foreignKeys = [
        ForeignKey(
            entity = EquipoEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["equipoId"])]
)
data class PruebaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val equipoId: Long,
    val codigoEquipo: String,
    val tipoEquipo: String,
    val valorMedicion: String,
    val fecha: String,
    val fotoUri: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)