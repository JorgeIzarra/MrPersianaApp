package com.utp.mrpersianaapp.data

data class Producto(
    val id: Long = 0,
    val cotizacionId: Long,
    val tipoProducto: String, // "Persianas" / "Cortinas"
    val subtipo: String? = null, // "Screen", "Blackout", "Velo"
    val color: String? = null,
    val ancho: Double,
    val alto: Double,
    val cantidad: Int,
    val precioUnitario: Double,
    val precioTotal: Double,
    // Especificaciones específicas
    val tieneCenefa: Boolean = false, // Solo persianas
    val esMotorizada: Boolean = false, // Ambos tipos
    val aperturaCentral: Boolean = true, // Solo cortinas: true=central, false=lateral
    val fechaCreacion: String
) {
    companion object {
        // Constantes para tipos de producto
        const val TIPO_PERSIANAS = "Persianas"
        const val TIPO_CORTINAS = "Cortinas"

        // Constantes para subtipos de persianas
        const val SUBTIPO_SCREEN = "Screen"
        const val SUBTIPO_BLACKOUT_PERSIANA = "Blackout"

        // Constantes para subtipos de cortinas
        const val SUBTIPO_VELO = "Velo"
        const val SUBTIPO_BLACKOUT_CORTINA = "Blackout"

        // Constantes para colores
        val COLORES_DISPONIBLES = listOf(
            "Blanco", "Beige", "Gris Claro", "Gris Oscuro",
            "Negro", "Azul", "Verde", "Café"
        )
    }

    /**
     * Función para verificar si es una persiana
     */
    fun esPersiana(): Boolean {
        return tipoProducto == TIPO_PERSIANAS
    }

    /**
     * Función para verificar si es una cortina
     */
    fun esCortina(): Boolean {
        return tipoProducto == TIPO_CORTINAS
    }

    /**
     * Función para obtener área del producto
     */
    fun getArea(): Double {
        return ancho * alto
    }

    /**
     * Función para obtener dimensiones formateadas
     */
    fun getDimensionesFormateadas(): String {
        return "${ancho}m x ${alto}m"
    }

    /**
     * Función para obtener precio total formateado
     */
    fun getPrecioTotalFormateado(): String {
        return "$${String.format("%.2f", precioTotal)}"
    }

    /**
     * Función para obtener precio unitario formateado
     */
    fun getPrecioUnitarioFormateado(): String {
        return "$${String.format("%.2f", precioUnitario)}"
    }

    /**
     * Función para obtener descripción del accionamiento
     */
    fun getDescripcionAccionamiento(): String {
        return if (esMotorizada) "⚡ Motorizada" else "🖐️ Manual"
    }

    /**
     * Función para obtener descripción de cenefa (solo persianas)
     */
    fun getDescripcionCenefa(): String {
        return if (tieneCenefa) "✅ Con cenefa" else "❌ Sin cenefa"
    }

    /**
     * Función para obtener descripción de apertura (solo cortinas)
     */
    fun getDescripcionApertura(): String {
        return if (aperturaCentral) "↔️ Apertura central" else "➡️ Apertura lateral"
    }

    /**
     * Función para obtener descripción completa del producto
     */
    fun getDescripcionCompleta(): String {
        val descripcion = StringBuilder()
        descripcion.append("$tipoProducto")

        subtipo?.let { descripcion.append(" - $it") }
        color?.let { descripcion.append(" - $it") }

        descripcion.append("\n📏 ${getDimensionesFormateadas()}")
        descripcion.append("\n📦 Cantidad: $cantidad")

        if (esPersiana()) {
            descripcion.append("\n🎀 ${getDescripcionCenefa()}")
        } else if (esCortina()) {
            descripcion.append("\n📐 ${getDescripcionApertura()}")
        }

        descripcion.append("\n🔧 ${getDescripcionAccionamiento()}")
        descripcion.append("\n💰 ${getPrecioTotalFormateado()}")

        return descripcion.toString()
    }
}