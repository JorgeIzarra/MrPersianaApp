package com.utp.mrpersianaapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetalleActivity : AppCompatActivity() {

    // Componentes de la interfaz
    private lateinit var tvTipoDetalle: TextView
    private lateinit var tvNombreCliente: TextView
    private lateinit var tvFechaCreacion: TextView
    private lateinit var tvEstado: TextView

    // Layout para información de cita
    private lateinit var layoutInfoCita: LinearLayout
    private lateinit var tvTelefonoCita: TextView
    private lateinit var tvDireccionCita: TextView
    private lateinit var tvFechaVisita: TextView
    private lateinit var tvHoraVisita: TextView
    private lateinit var tvTipoConsulta: TextView
    private lateinit var tvNotasCita: TextView

    // Layout para información de cotización
    private lateinit var layoutInfoCotizacion: LinearLayout
    private lateinit var tvCitaVinculada: TextView
    private lateinit var tvUbicacionInstalacion: TextView
    private lateinit var tvObservacionesCotizacion: TextView

    // Layout para productos (cotización)
    private lateinit var layoutProductos: LinearLayout
    private lateinit var tvListaProductos: TextView

    // Layout para totales (cotización)
    private lateinit var layoutTotales: LinearLayout
    private lateinit var tvSubtotalDetalle: TextView
    private lateinit var tvInstalacionDetalle: TextView
    private lateinit var tvTotalDetalle: TextView

    // Botones de acción
    private lateinit var btnCompartir: Button
    private lateinit var btnVerMaps: Button
    private lateinit var btnEditar: Button
    private lateinit var btnVolver: Button

    // Variables para datos recibidos
    private var tipoDetalle: String = ""
    private var datosRecibidos: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        initializeComponents()
        receiveData()
        setupUI()
        setupButtons()
    }

    /**
     * Inicializar todos los componentes de la interfaz
     */
    private fun initializeComponents() {
        // Componentes principales
        tvTipoDetalle = findViewById(R.id.tvTipoDetalle)
        tvNombreCliente = findViewById(R.id.tvNombreCliente)
        tvFechaCreacion = findViewById(R.id.tvFechaCreacion)
        tvEstado = findViewById(R.id.tvEstado)

        // Información de cita
        layoutInfoCita = findViewById(R.id.layoutInfoCita)
        tvTelefonoCita = findViewById(R.id.tvTelefonoCita)
        tvDireccionCita = findViewById(R.id.tvDireccionCita)
        tvFechaVisita = findViewById(R.id.tvFechaVisita)
        tvHoraVisita = findViewById(R.id.tvHoraVisita)
        tvTipoConsulta = findViewById(R.id.tvTipoConsulta)
        tvNotasCita = findViewById(R.id.tvNotasCita)

        // Información de cotización
        layoutInfoCotizacion = findViewById(R.id.layoutInfoCotizacion)
        tvCitaVinculada = findViewById(R.id.tvCitaVinculada)
        tvUbicacionInstalacion = findViewById(R.id.tvUbicacionInstalacion)
        tvObservacionesCotizacion = findViewById(R.id.tvObservacionesCotizacion)

        // Productos y totales
        layoutProductos = findViewById(R.id.layoutProductos)
        tvListaProductos = findViewById(R.id.tvListaProductos)
        layoutTotales = findViewById(R.id.layoutTotales)
        tvSubtotalDetalle = findViewById(R.id.tvSubtotalDetalle)
        tvInstalacionDetalle = findViewById(R.id.tvInstalacionDetalle)
        tvTotalDetalle = findViewById(R.id.tvTotalDetalle)

        // Botones
        btnCompartir = findViewById(R.id.btnCompartir)
        btnVerMaps = findViewById(R.id.btnVerMaps)
        btnEditar = findViewById(R.id.btnEditar)
        btnVolver = findViewById(R.id.btnVolver)
    }

    /**
     * Recibir datos del Intent
     */
    private fun receiveData() {
        tipoDetalle = intent.getStringExtra("tipo_detalle") ?: "CITA"
        datosRecibidos = intent.getBundleExtra("datos")
    }

    /**
     * Configurar la interfaz según el tipo
     */
    private fun setupUI() {
        if (tipoDetalle == "CITA") {
            mostrarDetalleCita()
        } else {
            mostrarDetalleCotizacion()
        }
    }

    /**
     * Mostrar información de una cita
     */
    private fun mostrarDetalleCita() {
        tvTipoDetalle.text = "📅 DETALLE DE CITA"

        // Mostrar layout de cita, ocultar cotización
        layoutInfoCita.visibility = View.VISIBLE
        layoutInfoCotizacion.visibility = View.GONE
        layoutProductos.visibility = View.GONE
        layoutTotales.visibility = View.GONE

        // Configurar botones para cita
        btnCompartir.visibility = View.VISIBLE
        btnVerMaps.visibility = View.VISIBLE
        btnEditar.text = "✏️ EDITAR CITA"

        // Llenar datos de la cita
        datosRecibidos?.let { datos ->
            tvNombreCliente.text = datos.getString("nombre_cliente", "Sin nombre")
            tvFechaCreacion.text = "Creada: ${datos.getString("fecha_creacion", "Hoy")}"
            tvEstado.text = datos.getString("estado", "PENDIENTE")

            tvTelefonoCita.text = datos.getString("telefono", "Sin teléfono")
            tvDireccionCita.text = datos.getString("direccion", "Sin dirección")
            tvFechaVisita.text = datos.getString("fecha_visita", "Sin fecha")
            tvHoraVisita.text = datos.getString("hora_visita", "Sin hora")
            tvTipoConsulta.text = datos.getString("tipo_consulta", "Sin especificar")

            val notas = datos.getString("notas_adicionales", "").takeIf { it.isNotEmpty() } ?: "Sin notas adicionales"
            tvNotasCita.text = notas
        }
    }

    /**
     * Mostrar información de una cotización
     */
    private fun mostrarDetalleCotizacion() {
        tvTipoDetalle.text = "📋 DETALLE DE COTIZACIÓN"

        // Mostrar layout de cotización, ocultar cita
        layoutInfoCita.visibility = View.GONE
        layoutInfoCotizacion.visibility = View.VISIBLE
        layoutProductos.visibility = View.VISIBLE
        layoutTotales.visibility = View.VISIBLE

        // Configurar botones para cotización
        btnCompartir.visibility = View.VISIBLE
        btnVerMaps.visibility = View.GONE
        btnEditar.text = "✏️ EDITAR COTIZACIÓN"

        // Llenar datos de la cotización
        datosRecibidos?.let { datos ->
            tvNombreCliente.text = datos.getString("nombre_cliente", "Sin nombre")
            tvFechaCreacion.text = "Creada: ${datos.getString("fecha_creacion", "Hoy")}"
            tvEstado.text = datos.getString("estado", "BORRADOR")

            tvCitaVinculada.text = datos.getString("cita_vinculada", "Sin cita vinculada")
            tvUbicacionInstalacion.text = datos.getString("ubicacion_instalacion", "En la ciudad")

            val observaciones = datos.getString("observaciones", "").takeIf { it.isNotEmpty() } ?: "Sin observaciones"
            tvObservacionesCotizacion.text = observaciones

            // Productos
            val productos = datos.getString("productos_detalle", "No hay productos registrados")
            tvListaProductos.text = productos

            // Totales
            tvSubtotalDetalle.text = datos.getString("subtotal", "Subtotal: $0.00")
            tvInstalacionDetalle.text = datos.getString("instalacion", "Instalación: $0.00")
            tvTotalDetalle.text = datos.getString("total", "TOTAL: $0.00")
        }
    }

    /**
     * Configurar los botones
     */
    private fun setupButtons() {
        btnCompartir.setOnClickListener {
            compartirDetalle()
        }

        btnVerMaps.setOnClickListener {
            abrirMaps()
        }

        btnEditar.setOnClickListener {
            editarElemento()
        }

        btnVolver.setOnClickListener {
            volverAlHistorial()
        }
    }

    /**
     * Compartir el detalle por WhatsApp/Email
     */
    private fun compartirDetalle() {
        val textoCompartir = if (tipoDetalle == "CITA") {
            generarTextoCita()
        } else {
            generarTextoCotizacion()
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, textoCompartir)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Mr. Persiana - ${tvTipoDetalle.text}")

        startActivity(Intent.createChooser(intent, "Compartir por:"))
    }

    /**
     * Generar texto para compartir cita
     */
    private fun generarTextoCita(): String {
        return """
            📅 CITA - MR. PERSIANA
            
            Cliente: ${tvNombreCliente.text}
            Teléfono: ${tvTelefonoCita.text}
            
            📍 Dirección: ${tvDireccionCita.text}
            🕐 Fecha: ${tvFechaVisita.text} - ${tvHoraVisita.text}
            🏷️ Tipo: ${tvTipoConsulta.text}
            
            📝 Notas: ${tvNotasCita.text}
            
            📞 Contacto: Mr. Persiana
        """.trimIndent()
    }

    /**
     * Generar texto para compartir cotización - VERSIÓN MEJORADA
     */
    private fun generarTextoCotizacion(): String {
        return """
            📋 *COTIZACIÓN - MR. PERSIANA*
            
            👤 *Cliente:* ${tvNombreCliente.text}
            
            🏷️ *Productos Cotizados:*
            ${formatearProductosParaCompartir()}
            
            📍 *Instalación:* ${tvUbicacionInstalacion.text}
            
            💰 *Resumen de Precios:*
            • Subtotal: ${formatearPrecio(tvSubtotalDetalle.text.toString())}
            • Instalación: ${formatearPrecio(tvInstalacionDetalle.text.toString())}
            ━━━━━━━━━━━━━━━━━━━━━
            *${formatearPrecio(tvTotalDetalle.text.toString())}*
            
            📞 *Contacto:* Mr. Persiana
            ✅ *Cotización válida por 30 días*
        """.trimIndent()
    }

    /**
     * Formatear productos para compartir de manera más legible
     */
    private fun formatearProductosParaCompartir(): String {
        val productosTexto = tvListaProductos.text.toString()

        // Si el texto está vacío o es el default
        if (productosTexto.isEmpty() || productosTexto == "No hay productos registrados") {
            return "• Sin productos especificados"
        }

        // Formatear el texto de productos para mejor legibilidad
        return productosTexto
            .replace("• Producto", "\n• *Producto")
            .replace("📏 Dimensiones:", "  📏 Dimensiones:")
            .replace("📦 Cantidad:", "  📦 Cantidad:")
            .replace("🏷️ Tipo:", "  🏷️ Tipo:")
            .replace("🎨 Color:", "  🎨 Color:")
            .replace("🎀 Cenefa:", "  🎀 Cenefa:")
            .replace("📐 Apertura:", "  📐 Apertura:")
            .replace("🔧 Accionamiento:", "  🔧 Accionamiento:")
            .replace("💰 ", "  💰 *")
            .replace("$", "$*")
            .trim()
    }

    /**
     * Formatear precios para mejor legibilidad
     */
    private fun formatearPrecio(precioTexto: String): String {
        // Extraer el número del texto
        val numero = precioTexto.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0

        // Formatear con comas para miles y dos decimales
        return when {
            precioTexto.contains("Subtotal") -> "Subtotal: $${String.format("%,.2f", numero)}"
            precioTexto.contains("Instalación") -> "Instalación: $${String.format("%,.2f", numero)}"
            precioTexto.contains("TOTAL") -> "TOTAL: $${String.format("%,.2f", numero)}"
            else -> "$${String.format("%,.2f", numero)}"
        }
    }

    /**
     * Abrir ubicación en Google Maps
     */
    private fun abrirMaps() {
        if (tipoDetalle == "CITA") {
            val direccion = tvDireccionCita.text.toString()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$direccion"))
            startActivity(intent)
        }
    }

    /**
     * Editar el elemento actual
     */
    private fun editarElemento() {
        val intent = if (tipoDetalle == "CITA") {
            Intent(this, CrearCitaActivity::class.java)
        } else {
            Intent(this, CrearCotizacionActivity::class.java)
        }

        // TODO: Aquí podríamos pasar los datos para prellenar el formulario
        intent.putExtra("modo_edicion", true)
        intent.putExtra("datos_elemento", datosRecibidos)

        startActivity(intent)
        finish()
    }

    /**
     * Volver al historial
     */
    private fun volverAlHistorial() {
        val intent = Intent(this, ListaUnificadaActivity::class.java)
        startActivity(intent)
        finish()
    }
}