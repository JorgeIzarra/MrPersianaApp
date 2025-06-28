package com.utp.mrpersianaapp.data

data class Cita(
    val id: Long = 0,
    val nombreCliente: String,
    val telefono: String,
    val direccion: String,
    val fechaVisita: String,
    val horaVisita: String,
    val tipoConsulta: String,
    val notasAdicionales: String? = null,
    val estado: String = "PENDIENTE", // PENDIENTE, COMPLETADA, COTIZADA
    val fechaCreacion: String
) {
    companion object {
        // Constantes para estados
        const val ESTADO_PENDIENTE = "PENDIENTE"
        const val ESTADO_COMPLETADA = "COMPLETADA"
        const val ESTADO_COTIZADA = "COTIZADA"

        // Constantes para tipos de consulta
        const val TIPO_PERSIANAS = "Persianas solamente"
        const val TIPO_CORTINAS = "Cortinas solamente"
        const val TIPO_AMBOS = "Persianas y Cortinas"
        const val TIPO_CONSULTORIA = "Consultoría general"
        const val TIPO_REPARACION = "Reparación/Mantenimiento"
    }

    /**
     * Función para obtener fecha y hora combinadas
     */
    fun getFechaHoraCompleta(): String {
        return "$fechaVisita - $horaVisita"
    }

    /**
     * Función para verificar si la cita está completada
     */
    fun estaCompletada(): Boolean {
        return estado == ESTADO_COMPLETADA || estado == ESTADO_COTIZADA
    }

    /**
     * Función para obtener descripción del estado
     */
    fun getDescripcionEstado(): String {
        return when (estado) {
            ESTADO_PENDIENTE -> "⏳ Pendiente"
            ESTADO_COMPLETADA -> "✅ Completada"
            ESTADO_COTIZADA -> "📋 Cotizada"
            else -> "❓ Sin estado"
        }
    }
}