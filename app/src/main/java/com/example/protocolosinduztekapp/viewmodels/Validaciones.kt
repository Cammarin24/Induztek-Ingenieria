package com.example.protocolosinduztekapp.viewmodels


object Validaciones {


    private val FORMATO_CODIGO = Regex("^[A-Z]{2}-\\d{3}$")


    private val SOLO_LETRAS = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")

    fun validarEquipo(equipoId: Long?): String? =
        if (equipoId == null) "Selecciona un equipo de la lista" else null

    fun validarCodigo(codigo: String): String? = when {
        codigo.isBlank() -> "El código es obligatorio"
        !FORMATO_CODIGO.matches(codigo.trim()) -> "Formato: 2 letras, guion y 3 números (ej. TR-001)"
        else -> null
    }

    fun validarTipo(tipo: String): String? = when {
        tipo.isBlank() -> "El tipo de equipo es obligatorio"
        !SOLO_LETRAS.matches(tipo.trim()) -> "Solo se permiten letras"
        tipo.trim().length < 3 -> "Debe tener al menos 3 letras"
        else -> null
    }

    fun validarMedicion(medicion: String): String? {
        if (medicion.isBlank()) return "La medición es obligatoria"
        val numero = medicion.replace(',', '.').toDoubleOrNull()
            ?: return "Debe ser un número (ej. 220 o 12,5)"
        return if (numero <= 0) "Debe ser mayor a 0" else null
    }
}