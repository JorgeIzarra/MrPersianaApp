package com.utp.mrpersianaapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.utp.mrpersianaapp.data.Cita
import com.utp.mrpersianaapp.data.Cotizacion
import com.utp.mrpersianaapp.data.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class ListaUnificadaActivity : AppCompatActivity() {

    // Componentes de la interfaz
    private lateinit var btnToggleFiltros: Button
    private lateinit var layoutFiltros: LinearLayout
    private lateinit var layoutFiltrosAplicados: LinearLayout
    private lateinit var tvFiltrosAplicados: TextView
    private lateinit var btnLimpiarFiltrosRapido: Button
    private lateinit var spinnerTipoVista: Spinner
    private lateinit var etBuscarCliente: EditText
    private lateinit var btnFiltrarHoy: Button
    private lateinit var btnFiltrarSemana: Button
    private lateinit var btnLimpiarFiltros: Button
    private lateinit var tvContadorResultados: TextView
    private lateinit var btnOrdenar: Button
    private lateinit var recyclerViewHistorial: RecyclerView
    private lateinit var layoutSinDatos: LinearLayout
    private lateinit var btnNuevaCita: Button
    private lateinit var btnNuevaCotizacion: Button
    private lateinit var btnVolver: Button

    // Base de datos
    private lateinit var databaseHelper: DatabaseHelper

    // Variables de control
    private var filtrosVisibles = false
    private lateinit var adapter: HistorialAdapter
    private var listaCompleta = mutableListOf<ItemHistorial>()
    private var listaFiltrada = mutableListOf<ItemHistorial>()

    // Opciones de filtros
    private val tiposVista = arrayOf(
        "Mostrar todo",
        "Solo Citas",
        "Solo Cotizaciones"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_unificada)

        // Inicializar base de datos
        databaseHelper = DatabaseHelper(this)

        initializeComponents()
        setupSpinners()
        setupRecyclerView()
        setupButtons()
        setupFilters()

        // Cargar datos reales de la base de datos
        cargarDatosRealesDesdeDB()
        aplicarFiltros()
    }

    private fun initializeComponents() {
        btnToggleFiltros = findViewById(R.id.btnToggleFiltros)
        layoutFiltros = findViewById(R.id.layoutFiltros)
        layoutFiltrosAplicados = findViewById(R.id.layoutFiltrosAplicados)
        tvFiltrosAplicados = findViewById(R.id.tvFiltrosAplicados)
        btnLimpiarFiltrosRapido = findViewById(R.id.btnLimpiarFiltrosRapido)
        spinnerTipoVista = findViewById(R.id.spinnerTipoVista)
        etBuscarCliente = findViewById(R.id.etBuscarCliente)
        btnFiltrarHoy = findViewById(R.id.btnFiltrarHoy)
        btnFiltrarSemana = findViewById(R.id.btnFiltrarSemana)
        btnLimpiarFiltros = findViewById(R.id.btnLimpiarFiltros)
        tvContadorResultados = findViewById(R.id.tvContadorResultados)
        btnOrdenar = findViewById(R.id.btnOrdenar)
        recyclerViewHistorial = findViewById(R.id.recyclerViewHistorial)
        layoutSinDatos = findViewById(R.id.layoutSinDatos)
        btnNuevaCita = findViewById(R.id.btnNuevaCita)
        btnNuevaCotizacion = findViewById(R.id.btnNuevaCotizacion)
        btnVolver = findViewById(R.id.btnVolver)
    }

    private fun setupSpinners() {
        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposVista)
        adapterTipo.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerTipoVista.adapter = adapterTipo

        spinnerTipoVista.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                aplicarFiltros()
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        adapter = HistorialAdapter(listaFiltrada,
            onItemClick = { item ->
                // Navegar a DetalleActivity con los datos del item
                navegarADetalle(item)
            },
            onMapsClick = { item ->
                abrirMaps(item.direccion)
            },
            onCompartirClick = { item ->
                compartirItem(item)
            }
        )

        recyclerViewHistorial.layoutManager = LinearLayoutManager(this)
        recyclerViewHistorial.adapter = adapter
    }

    private fun setupButtons() {
        // Toggle filtros
        btnToggleFiltros.setOnClickListener {
            toggleFiltros()
        }

        // Filtros rápidos
        btnFiltrarHoy.setOnClickListener {
            filtrarPorFecha("hoy")
        }

        btnFiltrarSemana.setOnClickListener {
            filtrarPorFecha("semana")
        }

        btnLimpiarFiltros.setOnClickListener {
            limpiarFiltros()
        }

        btnLimpiarFiltrosRapido.setOnClickListener {
            limpiarFiltros()
        }

        // Ordenar
        btnOrdenar.setOnClickListener {
            mostrarOpcionesOrden()
        }

        // Navegación
        btnNuevaCita.setOnClickListener {
            val intent = Intent(this, CrearCitaActivity::class.java)
            startActivity(intent)
        }

        btnNuevaCotizacion.setOnClickListener {
            val intent = Intent(this, CrearCotizacionActivity::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            volverAlMenu()
        }
    }

    private fun setupFilters() {
        // Filtro por texto en tiempo real
        etBuscarCliente.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                aplicarFiltros()
            }
        })
    }

    /**
     * MÉTODO PRINCIPAL: Cargar datos reales desde la base de datos
     */
    private fun cargarDatosRealesDesdeDB() {
        try {
            listaCompleta.clear()

            // 1. Cargar todas las citas desde la BD
            val citas = databaseHelper.obtenerTodasLasCitas()
            println("📅 Citas cargadas desde BD: ${citas.size}")

            citas.forEach { cita ->
                val itemCita = ItemHistorial(
                    tipo = TipoItem.CITA,
                    nombreCliente = cita.nombreCliente,
                    fecha = cita.fechaCreacion,
                    hora = cita.horaVisita,
                    estado = cita.estado,
                    direccion = cita.direccion,
                    tipoProducto = cita.tipoConsulta,
                    // Campos específicos de cita
                    telefono = cita.telefono,
                    fechaVisita = cita.fechaVisita,
                    notasAdicionales = cita.notasAdicionales,
                    // ID para referencia
                    citaId = cita.id
                )
                listaCompleta.add(itemCita)
            }

            // 2. Cargar todas las cotizaciones desde la BD
            val cotizaciones = databaseHelper.obtenerTodasLasCotizaciones()
            println("📋 Cotizaciones cargadas desde BD: ${cotizaciones.size}")

            cotizaciones.forEach { cotizacion ->
                val itemCotizacion = ItemHistorial(
                    tipo = TipoItem.COTIZACION,
                    nombreCliente = cotizacion.nombreCliente,
                    fecha = cotizacion.fechaCreacion,
                    hora = "Cotización",
                    estado = cotizacion.estado,
                    precio = cotizacion.total,
                    tipoProducto = obtenerTipoProductosDeCotizacion(cotizacion),
                    // Campos específicos de cotización
                    citaVinculada = obtenerCitaVinculadaTexto(cotizacion.citaId),
                    ubicacionInstalacion = cotizacion.ubicacionInstalacion,
                    observaciones = cotizacion.observaciones,
                    productosDetalle = generarDetalleProductos(cotizacion),
                    subtotal = cotizacion.subtotal,
                    costoInstalacion = cotizacion.costoInstalacion,
                    // ID para referencia
                    cotizacionId = cotizacion.id
                )
                listaCompleta.add(itemCotizacion)
            }

            // 3. Ordenar por fecha más reciente
            listaCompleta.sortByDescending { parseDate(it.fecha) }

            println("📊 Total elementos cargados: ${listaCompleta.size} (${citas.size} citas + ${cotizaciones.size} cotizaciones)")

        } catch (e: Exception) {
            println("❌ Error al cargar datos desde BD: ${e.message}")
            // En caso de error, mantener lista vacía
            listaCompleta.clear()
            Toast.makeText(this, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Obtener tipo de productos de una cotización
     */
    private fun obtenerTipoProductosDeCotizacion(cotizacion: Cotizacion): String {
        if (cotizacion.productos.isEmpty()) return "Sin productos"

        val tipos = cotizacion.productos.map { it.tipoProducto }.distinct()
        return when {
            tipos.size == 1 -> tipos.first()
            tipos.contains("Persianas") && tipos.contains("Cortinas") -> "Persianas y Cortinas"
            else -> tipos.joinToString(", ")
        }
    }

    /**
     * Obtener texto de cita vinculada
     */
    private fun obtenerCitaVinculadaTexto(citaId: Long?): String {
        return if (citaId != null) {
            try {
                val cita = databaseHelper.obtenerCitaPorId(citaId)
                "${cita?.nombreCliente} - ${cita?.fechaVisita}"
            } catch (e: Exception) {
                "Cita vinculada (ID: $citaId)"
            }
        } else {
            "Sin cita vinculada"
        }
    }

    /**
     * Generar detalle de productos para cotización
     */
    private fun generarDetalleProductos(cotizacion: Cotizacion): String {
        if (cotizacion.productos.isEmpty()) return "No hay productos registrados"

        val detalles = mutableListOf<String>()

        cotizacion.productos.forEachIndexed { index, producto ->
            var detalle = "• Producto ${index + 1}: ${producto.tipoProducto}\n" +
                    "  📏 Dimensiones: ${producto.getDimensionesFormateadas()}\n" +
                    "  📦 Cantidad: ${producto.cantidad} unidades\n"

            producto.subtipo?.let { detalle += "  🏷️ Tipo: $it\n" }
            producto.color?.let { detalle += "  🎨 Color: $it\n" }

            if (producto.esPersiana()) {
                detalle += "  🎀 Cenefa: ${producto.getDescripcionCenefa()}\n"
            } else if (producto.esCortina()) {
                detalle += "  📐 Apertura: ${producto.getDescripcionApertura()}\n"
            }

            detalle += "  🔧 Accionamiento: ${producto.getDescripcionAccionamiento()}\n"
            detalle += "  💰 ${producto.getPrecioTotalFormateado()}\n"

            detalles.add(detalle)
        }

        return detalles.joinToString("\n")
    }

    /**
     * Parsear fecha para ordenamiento
     */
    private fun parseDate(fechaStr: String): Date {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            format.parse(fechaStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    private fun toggleFiltros() {
        if (filtrosVisibles) {
            // Ocultar filtros
            layoutFiltros.visibility = View.GONE
            btnToggleFiltros.text = "🔍 MOSTRAR FILTROS"
            filtrosVisibles = false

            // Mostrar resumen de filtros aplicados
            actualizarResumenFiltros()
        } else {
            // Mostrar filtros
            layoutFiltros.visibility = View.VISIBLE
            btnToggleFiltros.text = "🔼 OCULTAR FILTROS"
            filtrosVisibles = true

            // Ocultar resumen
            layoutFiltrosAplicados.visibility = View.GONE
        }
    }

    private fun actualizarResumenFiltros() {
        val filtrosActivos = mutableListOf<String>()

        // Tipo de vista
        if (spinnerTipoVista.selectedItemPosition > 0) {
            filtrosActivos.add(tiposVista[spinnerTipoVista.selectedItemPosition])
        }

        // Cliente
        val cliente = etBuscarCliente.text.toString().trim()
        if (cliente.isNotEmpty()) {
            filtrosActivos.add("Cliente: $cliente")
        }

        if (filtrosActivos.isNotEmpty()) {
            tvFiltrosAplicados.text = "🏷️ Filtros: ${filtrosActivos.joinToString(", ")}"
            layoutFiltrosAplicados.visibility = View.VISIBLE
        } else {
            layoutFiltrosAplicados.visibility = View.GONE
        }
    }

    private fun aplicarFiltros() {
        listaFiltrada.clear()

        val tipoFiltro = spinnerTipoVista.selectedItemPosition
        val clienteFiltro = etBuscarCliente.text.toString().trim().lowercase()

        for (item in listaCompleta) {
            var incluir = true

            // Filtrar por tipo
            when (tipoFiltro) {
                1 -> if (item.tipo != TipoItem.CITA) incluir = false
                2 -> if (item.tipo != TipoItem.COTIZACION) incluir = false
            }

            // Filtrar por cliente
            if (clienteFiltro.isNotEmpty()) {
                if (!item.nombreCliente.lowercase().contains(clienteFiltro)) {
                    incluir = false
                }
            }

            if (incluir) {
                listaFiltrada.add(item)
            }
        }

        actualizarUI()
        if (!filtrosVisibles) {
            actualizarResumenFiltros()
        }
    }

    private fun filtrarPorFecha(periodo: String) {
        val calendar = Calendar.getInstance()
        val fechaHoy = calendar.time

        when (periodo) {
            "hoy" -> {
                // Filtrar solo elementos de hoy
                val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaHoyStr = formatoFecha.format(fechaHoy)

                listaFiltrada.clear()
                listaFiltrada.addAll(listaCompleta.filter {
                    it.fecha.startsWith(fechaHoyStr.substring(0, 5)) // Aproximación por día/mes
                })
            }
            "semana" -> {
                // Filtrar elementos de esta semana
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val fechaSemana = calendar.time

                listaFiltrada.clear()
                listaFiltrada.addAll(listaCompleta.filter {
                    parseDate(it.fecha).after(fechaSemana)
                })
            }
        }

        actualizarUI()
        Toast.makeText(this, "Filtro aplicado: $periodo", Toast.LENGTH_SHORT).show()
    }

    private fun limpiarFiltros() {
        spinnerTipoVista.setSelection(0)
        etBuscarCliente.setText("")
        aplicarFiltros()
        Toast.makeText(this, "Filtros limpiados", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarOpcionesOrden() {
        val opciones = arrayOf(
            "Fecha más reciente",
            "Fecha más antigua",
            "Nombre A-Z",
            "Nombre Z-A"
        )

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Ordenar por:")
        builder.setItems(opciones) { _, which ->
            ordenarLista(which)
        }
        builder.show()
    }

    private fun ordenarLista(opcion: Int) {
        when (opcion) {
            0 -> listaFiltrada.sortByDescending { parseDate(it.fecha) }
            1 -> listaFiltrada.sortBy { parseDate(it.fecha) }
            2 -> listaFiltrada.sortBy { it.nombreCliente }
            3 -> listaFiltrada.sortByDescending { it.nombreCliente }
        }

        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Lista ordenada", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarUI() {
        val total = listaFiltrada.size
        tvContadorResultados.text = "📊 Total: $total elementos"

        if (total == 0) {
            recyclerViewHistorial.visibility = View.GONE
            layoutSinDatos.visibility = View.VISIBLE
        } else {
            recyclerViewHistorial.visibility = View.VISIBLE
            layoutSinDatos.visibility = View.GONE
        }

        adapter.notifyDataSetChanged()
    }

    /**
     * Navegar a DetalleActivity con los datos del item seleccionado
     */
    private fun navegarADetalle(item: ItemHistorial) {
        val bundle = Bundle().apply {
            putString("nombre_cliente", item.nombreCliente)
            putString("fecha_creacion", item.fecha)
            putString("estado", item.estado)

            if (item.tipo == TipoItem.CITA) {
                // Datos específicos de cita - cargar datos reales de BD
                item.citaId?.let { citaId ->
                    try {
                        val cita = databaseHelper.obtenerCitaPorId(citaId)
                        if (cita != null) {
                            putString("telefono", cita.telefono)
                            putString("direccion", cita.direccion)
                            putString("fecha_visita", cita.fechaVisita)
                            putString("hora_visita", cita.horaVisita)
                            putString("tipo_consulta", cita.tipoConsulta)
                            putString("notas_adicionales", cita.notasAdicionales ?: "Sin notas adicionales")
                        }
                    } catch (e: Exception) {
                        println("❌ Error al cargar cita: ${e.message}")
                    }
                }
            } else {
                // Datos específicos de cotización - cargar datos reales de BD
                item.cotizacionId?.let { cotizacionId ->
                    try {
                        val cotizaciones = databaseHelper.obtenerTodasLasCotizaciones()
                        val cotizacion = cotizaciones.find { it.id == cotizacionId }

                        if (cotizacion != null) {
                            putString("cita_vinculada", item.citaVinculada ?: "Sin cita vinculada")
                            putString("ubicacion_instalacion", cotizacion.ubicacionInstalacion)
                            putString("observaciones", cotizacion.observaciones ?: "Sin observaciones")
                            putString("productos_detalle", item.productosDetalle ?: "No hay productos registrados")
                            putString("subtotal", cotizacion.getSubtotalFormateado())
                            putString("instalacion", cotizacion.getCostoInstalacionFormateado())
                            putString("total", cotizacion.getTotalFormateado())
                        }
                    } catch (e: Exception) {
                        println("❌ Error al cargar cotización: ${e.message}")
                    }
                }
            }
        }

        val intent = Intent(this, DetalleActivity::class.java)
        intent.putExtra("tipo_detalle", if (item.tipo == TipoItem.CITA) "CITA" else "COTIZACION")
        intent.putExtra("datos", bundle)

        startActivity(intent)
    }

    /**
     * Abrir ubicación en Google Maps
     */
    private fun abrirMaps(direccion: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$direccion"))
        startActivity(intent)
    }

    /**
     * Compartir item
     */
    private fun compartirItem(item: ItemHistorial) {
        val texto = if (item.tipo == TipoItem.CITA) {
            """
                📅 CITA - MR. PERSIANA
                
                Cliente: ${item.nombreCliente}
                Fecha: ${item.fecha} - ${item.hora}
                Tipo: ${item.tipoProducto}
                Dirección: ${item.direccion}
                
                📞 Contacto: Mr. Persiana
            """.trimIndent()
        } else {
            """
                📋 COTIZACIÓN - MR. PERSIANA
                
                Cliente: ${item.nombreCliente}
                Tipo: ${item.tipoProducto}
                Total: $${String.format("%.2f", item.precio)}
                
                📞 Contacto: Mr. Persiana
                ✅ Cotización válida por 30 días
            """.trimIndent()
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, texto)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Mr. Persiana - ${if (item.tipo == TipoItem.CITA) "Cita" else "Cotización"}")

        startActivity(Intent.createChooser(intent, "Compartir por:"))
    }

    private fun volverAlMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Recargar datos cuando se vuelve a la actividad
     */
    override fun onResume() {
        super.onResume()
        // Recargar datos por si se agregaron nuevas citas/cotizaciones
        cargarDatosRealesDesdeDB()
        aplicarFiltros()
    }
}

// Enum para tipos de items
enum class TipoItem {
    CITA, COTIZACION
}

// Data class mejorada para items del historial
data class ItemHistorial(
    val tipo: TipoItem,
    val nombreCliente: String,
    val fecha: String,
    val hora: String = "",
    val estado: String,
    val direccion: String = "",
    val precio: Double = 0.0,
    val tipoProducto: String = "",
    // Campos adicionales para citas
    val telefono: String? = null,
    val fechaVisita: String? = null,
    val notasAdicionales: String? = null,
    // Campos adicionales para cotizaciones
    val citaVinculada: String? = null,
    val ubicacionInstalacion: String? = null,
    val observaciones: String? = null,
    val productosDetalle: String? = null,
    val subtotal: Double? = null,
    val costoInstalacion: Double? = null,
    // IDs para referencia a BD
    val citaId: Long? = null,
    val cotizacionId: Long? = null
)

// Adapter mejorado para RecyclerView
class HistorialAdapter(
    private val lista: List<ItemHistorial>,
    private val onItemClick: (ItemHistorial) -> Unit,
    private val onMapsClick: (ItemHistorial) -> Unit,
    private val onCompartirClick: (ItemHistorial) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIconoTipo: TextView = itemView.findViewById(R.id.tvIconoTipo)
        val tvNombreCliente: TextView = itemView.findViewById(R.id.tvNombreCliente)
        val tvTipoElemento: TextView = itemView.findViewById(R.id.tvTipoElemento)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        val tvFechaHora: TextView = itemView.findViewById(R.id.tvFechaHora)
        val layoutDireccion: LinearLayout = itemView.findViewById(R.id.layoutDireccion)
        val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        val layoutPrecio: LinearLayout = itemView.findViewById(R.id.layoutPrecio)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val btnVerMaps: Button = itemView.findViewById(R.id.btnVerMaps)
        val btnCompartir: Button = itemView.findViewById(R.id.btnCompartir)
        val btnVerDetalles: Button = itemView.findViewById(R.id.btnVerDetalles)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        // Configurar según tipo
        if (item.tipo == TipoItem.CITA) {
            holder.tvIconoTipo.text = "📅"
            holder.tvTipoElemento.text = "Cita • ${item.tipoProducto}"
            holder.layoutDireccion.visibility = View.VISIBLE
            holder.tvDireccion.text = item.direccion
            holder.layoutPrecio.visibility = View.GONE
            holder.btnVerMaps.visibility = View.VISIBLE
            holder.btnCompartir.visibility = View.GONE
        } else {
            holder.tvIconoTipo.text = "📋"
            holder.tvTipoElemento.text = "Cotización • ${item.tipoProducto}"
            holder.layoutDireccion.visibility = View.GONE
            holder.layoutPrecio.visibility = View.VISIBLE
            holder.tvPrecio.text = "Total: $${String.format("%.2f", item.precio)}"
            holder.btnVerMaps.visibility = View.GONE
            holder.btnCompartir.visibility = View.VISIBLE
        }

        holder.tvNombreCliente.text = item.nombreCliente
        holder.tvEstado.text = item.estado
        holder.tvFechaHora.text = "${item.fecha} - ${item.hora}"

        // Listeners
        holder.btnVerDetalles.setOnClickListener { onItemClick(item) }
        holder.btnVerMaps.setOnClickListener { onMapsClick(item) }
        holder.btnCompartir.setOnClickListener { onCompartirClick(item) }
    }

    override fun getItemCount() = lista.size
}