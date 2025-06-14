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

        initializeComponents()
        setupSpinners()
        setupRecyclerView()
        setupButtons()
        setupFilters()

        // Cargar datos de ejemplo
        cargarDatosEjemplo()
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
        adapter = HistorialAdapter(listaFiltrada) { item ->
            when (item.tipo) {
                TipoItem.CITA -> mostrarDetalleCita(item)
                TipoItem.COTIZACION -> mostrarDetalleCotizacion(item)
            }
        }

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
                    it.fecha.startsWith(fechaHoyStr.substring(0, 5)) // Aproximación
                })
            }
            "semana" -> {
                // Filtrar elementos de esta semana (simplificado)
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val fechaSemana = calendar.time

                listaFiltrada.clear()
                listaFiltrada.addAll(listaCompleta) // Simplificado para el ejemplo
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
            0 -> listaFiltrada.sortByDescending { it.fecha }
            1 -> listaFiltrada.sortBy { it.fecha }
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

    private fun cargarDatosEjemplo() {
        // Datos de ejemplo para testing
        listaCompleta.addAll(listOf(
            ItemHistorial(
                tipo = TipoItem.CITA,
                nombreCliente = "Juan Pérez",
                fecha = "15/06/2025",
                hora = "10:30 AM",
                estado = "PENDIENTE",
                direccion = "Calle 50, Casa 123, San Francisco",
                tipoProducto = "Persianas"
            ),
            ItemHistorial(
                tipo = TipoItem.COTIZACION,
                nombreCliente = "María García",
                fecha = "14/06/2025",
                hora = "14:00 PM",
                estado = "ENVIADA",
                precio = 450.0,
                tipoProducto = "Cortinas"
            ),
            ItemHistorial(
                tipo = TipoItem.CITA,
                nombreCliente = "Carlos López",
                fecha = "16/06/2025",
                hora = "09:00 AM",
                estado = "COMPLETADA",
                direccion = "Avenida Balboa, Edificio Plaza, Apt 505",
                tipoProducto = "Persianas y Cortinas"
            ),
            ItemHistorial(
                tipo = TipoItem.COTIZACION,
                nombreCliente = "Ana Rodríguez",
                fecha = "13/06/2025",
                hora = "16:30 PM",
                estado = "CERRADA",
                precio = 320.0,
                tipoProducto = "Persianas"
            )
        ))
    }

    private fun mostrarDetalleCita(item: ItemHistorial) {
        val mensaje = """
            📅 DETALLE DE CITA
            
            Cliente: ${item.nombreCliente}
            Fecha: ${item.fecha} - ${item.hora}
            Tipo: ${item.tipoProducto}
            Estado: ${item.estado}
            Dirección: ${item.direccion}
        """.trimIndent()

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun mostrarDetalleCotizacion(item: ItemHistorial) {
        val mensaje = """
            📋 DETALLE DE COTIZACIÓN
            
            Cliente: ${item.nombreCliente}
            Fecha: ${item.fecha}
            Tipo: ${item.tipoProducto}
            Estado: ${item.estado}
            Precio: $${String.format("%.2f", item.precio)}
        """.trimIndent()

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun volverAlMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}

// Enum para tipos de items
enum class TipoItem {
    CITA, COTIZACION
}

// Data class para items del historial
data class ItemHistorial(
    val tipo: TipoItem,
    val nombreCliente: String,
    val fecha: String,
    val hora: String = "",
    val estado: String,
    val direccion: String = "",
    val precio: Double = 0.0,
    val tipoProducto: String = ""
)

// Adapter para RecyclerView
class HistorialAdapter(
    private val lista: List<ItemHistorial>,
    private val onItemClick: (ItemHistorial) -> Unit
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

        holder.btnVerMaps.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${item.direccion}"))
            holder.itemView.context.startActivity(intent)
        }

        holder.btnCompartir.setOnClickListener {
            val texto = "Cotización Mr. Persiana - ${item.nombreCliente}: $${String.format("%.2f", item.precio)}"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, texto)
            holder.itemView.context.startActivity(Intent.createChooser(intent, "Compartir cotización"))
        }
    }

    override fun getItemCount() = lista.size
}