package com.example.protocolosinduztekapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.protocolosinduztekapp.repository.InduztekRepository
import com.example.protocolosinduztekapp.repository.data.AppDatabase
import com.example.protocolosinduztekapp.repository.models.EquipoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class EquipoFormUiState(
    val codigo: String = "",
    val tipo: String = "",
    val marca: String = "",
    val instalacion: String = "",
    val fotoPlacaUri: String? = null,
    val errorCodigo: String? = null,
    val errorTipo: String? = null,
    val errorMarca: String? = null,
    val errorInstalacion: String? = null,
    val guardando: Boolean = false,
    val guardado: Boolean = false
)

class EquipoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.obtenerBaseDatos(application)
    private val repository = InduztekRepository(db.equipoDao(), db.pruebaDao())


    val tiposEquipo = listOf(
        "Transformador",
        "Relé de protección",
        "Cable MT",
        "Celda de media tensión",
        "Interruptor",
        "Tablero eléctrico"
    )


    val equipos: StateFlow<List<EquipoEntity>> = repository.equipos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _formulario = MutableStateFlow(EquipoFormUiState())
    val formulario: StateFlow<EquipoFormUiState> = _formulario.asStateFlow()


    fun onCodigoChange(valor: String) {
        val codigo = valor.uppercase()
        _formulario.update { it.copy(codigo = codigo, errorCodigo = Validaciones.validarCodigo(codigo)) }
    }

    fun onTipoSeleccionado(tipo: String) {
        _formulario.update { it.copy(tipo = tipo, errorTipo = null) }
    }

    fun onMarcaChange(valor: String) {
        _formulario.update { it.copy(marca = valor, errorMarca = Validaciones.validarMarca(valor)) }
    }

    fun onInstalacionChange(valor: String) {
        _formulario.update { it.copy(instalacion = valor, errorInstalacion = Validaciones.validarInstalacion(valor)) }
    }

    fun onFotoPlacaTomada(uri: String) {
        _formulario.update { it.copy(fotoPlacaUri = uri) }
    }


    fun guardarEquipo() {
        val estado = _formulario.value
        val errorCodigo = Validaciones.validarCodigo(estado.codigo)
        val errorTipo = Validaciones.validarTipo(estado.tipo)
        val errorMarca = Validaciones.validarMarca(estado.marca)
        val errorInstalacion = Validaciones.validarInstalacion(estado.instalacion)

        if (errorCodigo != null || errorTipo != null || errorMarca != null || errorInstalacion != null) {
            _formulario.update {
                it.copy(
                    errorCodigo = errorCodigo,
                    errorTipo = errorTipo,
                    errorMarca = errorMarca,
                    errorInstalacion = errorInstalacion
                )
            }
            return
        }

        _formulario.update { it.copy(guardando = true) }
        viewModelScope.launch {
            val resultado = repository.guardarEquipo(
                EquipoEntity(
                    codigo = estado.codigo.trim(),
                    tipo = estado.tipo,
                    marca = estado.marca.trim(),
                    instalacion = estado.instalacion.trim(),
                    fotoPlacaUri = estado.fotoPlacaUri
                )
            )
            resultado
                .onSuccess { _formulario.update { it.copy(guardando = false, guardado = true) } }
                .onFailure { error ->

                    _formulario.update { it.copy(guardando = false, errorCodigo = error.message) }
                }
        }
    }

    fun limpiarFormulario() {
        _formulario.value = EquipoFormUiState()
    }
}

