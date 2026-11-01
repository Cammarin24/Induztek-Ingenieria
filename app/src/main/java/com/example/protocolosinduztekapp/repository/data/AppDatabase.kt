package com.example.protocolosinduztekapp.repository.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.protocolosinduztekapp.repository.models.EquipoEntity
import com.example.protocolosinduztekapp.repository.models.PruebaEntity


@Database(
    entities = [EquipoEntity::class, PruebaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun equipoDao(): EquipoDao
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
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(datosIniciales)
                    .build()
                INSTANCE = instance
                instance
            }
        }


        private val datosIniciales = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL(
                    "INSERT OR IGNORE INTO tabla_equipos (codigo, tipo, marca, instalacion, fotoPlacaUri) VALUES " +
                            "('TR-001', 'Transformador', 'Rhona', 'S/E Norte', NULL), " +
                            "('TR-002', 'Transformador', 'Schneider', 'Planta Sur', NULL), " +
                            "('RL-014', 'Relé de protección', 'ABB', 'S/E Norte', NULL), " +
                            "('CB-207', 'Cable MT 15 kV', 'Nexans', 'Planta Sur', NULL), " +
                            "('CL-003', 'Celda de media tensión', 'Siemens', 'S/E Norte', NULL)"
                )
            }
        }
    }
}