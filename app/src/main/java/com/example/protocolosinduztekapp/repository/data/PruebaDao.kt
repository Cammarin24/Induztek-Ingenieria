package com.example.protocolosinduztekapp.repository.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.protocolosinduztekapp.repository.models.PruebaEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PruebaDao {
    @Query("SELECT * FROM tabla_pruebas ORDER BY id DESC")
    fun obtenerPruebas(): Flow<List<PruebaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrueba(prueba: PruebaEntity)

    @Delete
    suspend fun eliminarPrueba(prueba: PruebaEntity)
}
