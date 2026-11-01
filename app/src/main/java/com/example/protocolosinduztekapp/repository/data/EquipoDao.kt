package com.example.protocolosinduztekapp.repository.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.protocolosinduztekapp.repository.models.EquipoEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface EquipoDao {
    @Query("SELECT * FROM tabla_equipos ORDER BY codigo ASC")
    fun obtenerEquipos(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM tabla_equipos WHERE id = :id")
    suspend fun obtenerEquipoPorId(id: Long): EquipoEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarEquipo(equipo: EquipoEntity): Long

    @Update
    suspend fun actualizarEquipo(equipo: EquipoEntity)

    @Delete
    suspend fun eliminarEquipo(equipo: EquipoEntity)
}

