package com.utp.mrpersianaapp.data

data class Cotizacion(
    val id: Long = 0,
    val citaId: Long? = null, // Puede ser null si no está vinculada a cita
    val nombreCliente: String,
    val ubicacionInstalacion: String,
    val observaciones: String? = null,
    val subtotal: Double,
    val costoInstalacion: Double,
    val total: Double,
    val estado: String = "ENVIADA", // ENVIADA, SEGUIMIENTO, CERRADA
    val fechaCreacion: String,
    val productos: List<Producto> = emptyList() // Lista de productos asociados
) {
    companion object {
        // Constantes para estados
        const val ESTADO_ENVIADA = "ENVIADA"
        const val ESTADO_SEGUIMIENTO = "SEGUIMIENTO"
        const val ESTADO_CERRADA = "CERRADA"

        // Constantes para ubicación
        const val UBICACION_CIUDAD = "En la ciudad (precio estándar)"
        const val UBICACION_AFUERAS = "Afueras/Interior (+$25 traslado)"
    }

    /**
     * Función para verificar si tiene cita vinculada
     */
    fun tieneCitaVinculada(): Boolean {
        return citaId != null
    }

    /**
     * Función para obtener descripción del estado
     */
    fun getDescripcionEstado(): String {
        return when (estado) {
            ESTADO_ENVIADA -> "📤 Enviada"
            ESTADO_SEGUIMIENTO -> "👁️ En seguimiento"
            ESTADO_CERRADA -> "✅ Cerrada"
            else -> "❓ Sin estado"
        }
    }

    /**
     * Función para obtener total formateado
     */
    fun getTotalFormateado(): String {
        return "$${String.format("%.2f", total)}"
    }

    /**
     * Función para obtener subtotal formateado
     */
    fun getSubtotalFormateado(): String {
        return "$${String.format("%.2f", subtotal)}"
    }

    /**
     * Función para obtener costo instalación formateado
     */
    fun getCostoInstalacionFormateado(): String {
        return "$${String.format("%.2f", costoInstalacion)}"
    }

    /**
     * Función para verificar si tiene productos
     */
    fun tieneProductos(): Boolean {
        return productos.isNotEmpty()
    }

    /**
     * Función para obtener cantidad total de productos
     */
    fun getCantidadTotalProductos(): Int {
        return productos.sumOf { it.cantidad }
    }
}