package com.example.protocolosinduztekapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.protocolosinduztekapp.repository.InduztekRepository
import com.example.protocolosinduztekapp.repository.data.AppDatabase
import com.example.protocolosinduztekapp.repository.models.EquipoEntity
import com.example.protocolosinduztekapp.repository.models.PruebaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class FormularioUiState(
    val equipoId: Long? = null,
    val equipoTexto: String = "",
    val medicion: String = "",
    val fotoUri: String? = null,
    val errorEquipo: String? = null,
    val errorMedicion: String? = null,
    val guardado: Boolean = false
)

class PruebaViewModel(application: Application) : AndroidViewModel(application) {


    private val db = AppDatabase.obtenerBaseDatos(application)
    private val repository = InduztekRepository(db.equipoDao(), db.pruebaDao())


    val listaPruebas: StateFlow<List<PruebaEntity>> = repository.pruebas
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val equipos: StateFlow<List<EquipoEntity>> = repository.equipos
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    private val _formulario = MutableStateFlow(FormularioUiState())
    val formulario: StateFlow<FormularioUiState> = _formulario.asStateFlow()


    fun onEquipoSeleccionado(equipo: EquipoEntity) {
        _formulario.update {
            it.copy(
                equipoId = equipo.id,
                equipoTexto = "${equipo.codigo} · ${equipo.tipo}",
                errorEquipo = null
            )
        }
    }

    fun onMedicionChange(valor: String) {
        _formulario.update { it.copy(medicion = valor, errorMedicion = Validaciones.validarMedicion(valor)) }
    }

    // La cámara devuelve la ruta de la foto tomada
    fun onFotoTomada(uri: String) {
        _formulario.update { it.copy(fotoUri = uri) }
    }


    fun guardarFormulario() {
        val estado = _formulario.value
        val errorEquipo = Validaciones.validarEquipo(estado.equipoId)
        val errorMedicion = Validaciones.validarMedicion(estado.medicion)

        if (errorEquipo != null || errorMedicion != null) {
            _formulario.update { it.copy(errorEquipo = errorEquipo, errorMedicion = errorMedicion) }
            return
        }

        val equipo = equipos.value.firstOrNull { it.id == estado.equipoId }
        if (equipo == null) {
            _formulario.update { it.copy(errorEquipo = "El equipo seleccionado ya no existe") }
            return
        }

        // Fecha y hora automáticas (Ej: 27/10/2026 12:30)
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            repository.guardarPrueba(
                PruebaEntity(
                    equipoId = equipo.id,
                    codigoEquipo = equipo.codigo,
                    tipoEquipo = equipo.tipo,
                    valorMedicion = estado.medicion.trim(),
                    fecha = fechaActual,
                    fotoUri = estado.fotoUri
                )
            )
            _formulario.update { it.copy(guardado = true) }
        }
    }

    fun limpiarFormulario() {
        _formulario.value = FormularioUiState()
    }

    fun eliminarPrueba(prueba: PruebaEntity) {
        viewModelScope.launch {
            repository.eliminarPrueba(prueba)
        }
    }
}