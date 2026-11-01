package com.example.protocolosinduztekapp.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.protocolosinduztekapp.repository.data.EquipoDao
import com.example.protocolosinduztekapp.repository.data.PruebaDao
import com.example.protocolosinduztekapp.repository.models.EquipoEntity
import com.example.protocolosinduztekapp.repository.models.PruebaEntity
import kotlinx.coroutines.flow.Flow


class InduztekRepository(
    private val equipoDao: EquipoDao,
    private val pruebaDao: PruebaDao
) {

    val equipos: Flow<List<EquipoEntity>> = equipoDao.obtenerEquipos()

    suspend fun obtenerEquipo(id: Long): EquipoEntity? = equipoDao.obtenerEquipoPorId(id)

    suspend fun guardarEquipo(equipo: EquipoEntity): Result<Long> {
        return try {
            if (equipo.id == 0L) {
                Result.success(equipoDao.insertarEquipo(equipo))
            } else {
                equipoDao.actualizarEquipo(equipo)
                Result.success(equipo.id)
            }
        } catch (e: SQLiteConstraintException) {
            Result.failure(IllegalStateException("Ya existe un equipo con el código ${equipo.codigo}"))
        }
    }


    val pruebas: Flow<List<PruebaEntity>> = pruebaDao.obtenerPruebas()

    fun pruebasDeEquipo(equipoId: Long): Flow<List<PruebaEntity>> =
        pruebaDao.obtenerPruebasPorEquipo(equipoId)

    suspend fun obtenerPrueba(id: Long): PruebaEntity? = pruebaDao.obtenerPruebaPorId(id)

    suspend fun guardarPrueba(prueba: PruebaEntity) = pruebaDao.insertarPrueba(prueba)

    suspend fun eliminarPrueba(prueba: PruebaEntity) = pruebaDao.eliminarPrueba(prueba)
}

