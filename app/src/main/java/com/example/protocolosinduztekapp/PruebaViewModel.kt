package com.example.protocolosinduztekapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PruebaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.obtenerBaseDatos(application).pruebaDao()

    val listaPruebas: StateFlow<List<PruebaEntity>> = dao.obtenerPruebas()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun guardarPrueba(codigo: String, tipo: String, medicion: String, fecha: String) {
        viewModelScope.launch {
            val nuevaPrueba = PruebaEntity(
                codigoEquipo = codigo,
                tipoEquipo = tipo,
                valorMedicion = medicion,
                fecha = fecha
            )
            dao.insertarPrueba(nuevaPrueba)
        }
    }
    fun eliminarPrueba(prueba: PruebaEntity) {
        viewModelScope.launch {
            dao.eliminarPrueba(prueba)
        }
    }
}