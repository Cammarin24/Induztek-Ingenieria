package com.example.protocolosinduztekapp

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PruebaDao {
    @Query("SELECT * FROM tabla_pruebas ORDER BY id DESC")
    fun obtenerPruebas(): Flow<List<PruebaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrueba(prueba: PruebaEntity)

    // <-- Añade esta función para eliminar por objeto
    @Delete
    suspend fun eliminarPrueba(prueba: PruebaEntity)
}

@Database(entities = [PruebaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pruebaDao(): PruebaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerBaseDatos(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "induztek_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}