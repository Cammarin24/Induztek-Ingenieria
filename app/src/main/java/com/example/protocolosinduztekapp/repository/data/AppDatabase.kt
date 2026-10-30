package com.example.protocolosinduztekapp.repository.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.protocolosinduztekapp.repository.models.PruebaEntity

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