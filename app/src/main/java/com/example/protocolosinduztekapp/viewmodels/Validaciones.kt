package com.example.protocolosinduztekapp.viewmodels


object Validaciones {


    private val FORMATO_CODIGO = Regex("^[A-Z]{2}-\\d{3}$")


    private val SOLO_LETRAS = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")


    fun validarEquipo(equipoId: Long?): String? =
        if (equipoId == null) "Selecciona un equipo de la lista" else null

    fun validarMedicion(medicion: String): String? {
        if (medicion.isBlank()) return "La medición es obligatoria"
        val numero = medicion.replace(',', '.').toDoubleOrNull()
            ?: return "Debe ser un número (ej. 220 o 12,5)"
        return if (numero <= 0) "Debe ser mayor a 0" else null
    }


    fun validarCodigo(codigo: String): String? = when {
        codigo.isBlank() -> "El código es obligatorio"
        !FORMATO_CODIGO.matches(codigo.trim()) -> "Formato: 2 letras, guion y 3 números (ej. TR-001)"
        else -> null
    }

    fun validarTipo(tipo: String): String? = when {
        tipo.isBlank() -> "Selecciona el tipo de equipo"
        !SOLO_LETRAS.matches(tipo.trim()) -> "Solo se permiten letras"
        else -> null
    }

    fun validarMarca(marca: String): String? = when {
        marca.isBlank() -> "La marca es obligatoria"
        marca.trim().length < 2 -> "Debe tener al menos 2 caracteres"
        marca.trim().length > 30 -> "Máximo 30 caracteres"
        else -> null
    }

    fun validarInstalacion(instalacion: String): String? = when {
        instalacion.isBlank() -> "La instalación es obligatoria"
        instalacion.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }
}