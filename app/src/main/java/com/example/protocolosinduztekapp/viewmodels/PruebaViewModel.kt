package com.example.protocolosinduztekapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.protocolosinduztekapp.repository.data.AppDatabase
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
    val codigo: String = "",
    val tipo: String = "",
    val medicion: String = "",
    val errorCodigo: String? = null,
    val errorTipo: String? = null,
    val errorMedicion: String? = null,
    val guardado: Boolean = false
)

class PruebaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.obtenerBaseDatos(application).pruebaDao()


    val listaPruebas: StateFlow<List<PruebaEntity>> = dao.obtenerPruebas()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    private val _formulario = MutableStateFlow(FormularioUiState())
    val formulario: StateFlow<FormularioUiState> = _formulario.asStateFlow()


    fun onCodigoChange(valor: String) {
        val codigo = valor.uppercase()
        _formulario.update { it.copy(codigo = codigo, errorCodigo = Validaciones.validarCodigo(codigo)) }
    }

    fun onTipoChange(valor: String) {
        _formulario.update { it.copy(tipo = valor, errorTipo = Validaciones.validarTipo(valor)) }
    }

    fun onMedicionChange(valor: String) {
        _formulario.update { it.copy(medicion = valor, errorMedicion = Validaciones.validarMedicion(valor)) }
    }


    fun guardarFormulario() {
        val estado = _formulario.value
        val errorCodigo = Validaciones.validarCodigo(estado.codigo)
        val errorTipo = Validaciones.validarTipo(estado.tipo)
        val errorMedicion = Validaciones.validarMedicion(estado.medicion)

        if (errorCodigo != null || errorTipo != null || errorMedicion != null) {
            _formulario.update {
                it.copy(errorCodigo = errorCodigo, errorTipo = errorTipo, errorMedicion = errorMedicion)
            }
            return
        }


        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        guardarPrueba(estado.codigo.trim(), estado.tipo.trim(), estado.medicion.trim(), fechaActual)
        _formulario.update { it.copy(guardado = true) }
    }

    fun limpiarFormulario() {
        _formulario.value = FormularioUiState()
    }

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