package com.utp.mrpersianaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CrearCotizacionActivity : AppCompatActivity() {

    // Componentes principales
    private lateinit var scrollView: ScrollView
    private lateinit var spinnerCitaVinculada: Spinner
    private lateinit var etNombreCliente: EditText
    private lateinit var btnAgregarProducto: Button
    private lateinit var rgUbicacionInstalacion: RadioGroup
    private lateinit var etObservaciones: EditText
    private lateinit var tvSubtotal: TextView
    private lateinit var tvInstalacion: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnGuardarCotizacion: Button
    private lateinit var btnCompartirCotizacion: Button
    private lateinit var btnLimpiarFormulario: Button
    private lateinit var btnVolver: Button

    // Layouts de productos
    private lateinit var producto1: View
    private lateinit var producto2: View
    private lateinit var producto3: View
    private lateinit var producto4: View

    // Control de productos activos
    private var productosActivos = 1
    private val maxProductos = 4

    // Arrays para opciones de spinners
    private val tiposProducto = arrayOf(
        "Selecciona el tipo de producto...",
        "Persianas",
        "Cortinas"
    )

    private val tiposPersianas = arrayOf(
        "Selecciona el tipo de persiana...",
        "Screen",
        "Blackout"
    )

    private val tiposCortinas = arrayOf(
        "Selecciona el tipo de cortina...",
        "Velo",
        "Blackout"
    )

    private val coloresDisponibles = arrayOf(
        "Selecciona el color...",
        "Blanco",
        "Beige",
        "Gris Claro",
        "Gris Oscuro",
        "Negro",
        "Azul",
        "Verde",
        "Café"
    )

    private val citasDisponibles = arrayOf(
        "Sin cita vinculada",
        "Wilcaris Zambrano - 30/06/2025",
        "María García - 03/07/2025",
        "Carlos López - 05/07/2025"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cotizacion)

        initializeComponents()
        setupSpinners()
        setupProductos()
        setupButtons()
        setupInstalacion()
        calcularPrecios()
    }

    private fun initializeComponents() {
        scrollView = findViewById(R.id.scrollViewCrearCotizacion)
        spinnerCitaVinculada = findViewById(R.id.spinnerCitaVinculada)
        etNombreCliente = findViewById(R.id.etNombreCliente)
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto)
        rgUbicacionInstalacion = findViewById(R.id.rgUbicacionInstalacion)
        etObservaciones = findViewById(R.id.etObservaciones)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvInstalacion = findViewById(R.id.tvInstalacion)
        tvTotal = findViewById(R.id.tvTotal)
        btnGuardarCotizacion = findViewById(R.id.btnGuardarCotizacion)
        btnCompartirCotizacion = findViewById(R.id.btnCompartirCotizacion)
        btnLimpiarFormulario = findViewById(R.id.btnLimpiarFormulario)
        btnVolver = findViewById(R.id.btnVolver)


        // Referencias a productos incluidos
        producto1 = findViewById(R.id.producto1)
        producto2 = findViewById(R.id.producto2)
        producto3 = findViewById(R.id.producto3)
        producto4 = findViewById(R.id.producto4)
    }

    private fun setupSpinners() {
        // Spinner de citas vinculadas
        val adapterCitas = ArrayAdapter(this, android.R.layout.simple_spinner_item, citasDisponibles)
        adapterCitas.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerCitaVinculada.adapter = adapterCitas

        spinnerCitaVinculada.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    // Auto-llenar nombre del cliente desde la cita
                    val nombreCliente = citasDisponibles[position].split(" - ")[0]
                    etNombreCliente.setText(nombreCliente)
                }
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupProductos() {
        // Configurar producto 1 (siempre visible)
        setupProducto(producto1, 1)

        // Ocultar productos 2, 3, 4 inicialmente
        producto2.visibility = View.GONE
        producto3.visibility = View.GONE
        producto4.visibility = View.GONE
    }

    private fun setupProducto(productoView: View, numeroProducto: Int) {
        // Actualizar título
        val tvTitulo = productoView.findViewById<TextView>(R.id.tvTituloProducto)
        tvTitulo.text = "🏷️ Producto $numeroProducto"

        // Configurar spinner tipo producto
        val spinnerTipo = productoView.findViewById<Spinner>(R.id.spinnerTipoProducto)
        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposProducto)
        adapterTipo.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerTipo.adapter = adapterTipo

        // Referencias a layouts condicionales
        val layoutPersianas = productoView.findViewById<LinearLayout>(R.id.layoutPersianas)
        val layoutCortinas = productoView.findViewById<LinearLayout>(R.id.layoutCortinas)
        val layoutDimensiones = productoView.findViewById<LinearLayout>(R.id.layoutDimensiones)

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        // No seleccionado
                        layoutPersianas.visibility = View.GONE
                        layoutCortinas.visibility = View.GONE
                        layoutDimensiones.visibility = View.GONE
                    }
                    1 -> {
                        // Persianas
                        layoutPersianas.visibility = View.VISIBLE
                        layoutCortinas.visibility = View.GONE
                        layoutDimensiones.visibility = View.VISIBLE
                        setupSpinnersPersianas(productoView)
                    }
                    2 -> {
                        // Cortinas
                        layoutPersianas.visibility = View.GONE
                        layoutCortinas.visibility = View.VISIBLE
                        layoutDimensiones.visibility = View.VISIBLE
                        setupSpinnersCortinas(productoView)
                    }
                }
                calcularPrecios()
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Configurar botones de cantidad
        setupCantidadButtons(productoView)

        // Configurar botón eliminar (solo para productos 2, 3, 4)
        val btnEliminar = productoView.findViewById<Button>(R.id.btnEliminarProducto)
        if (numeroProducto > 1) {
            btnEliminar.visibility = View.VISIBLE
            btnEliminar.setOnClickListener {
                eliminarProducto(productoView, numeroProducto)
            }
        }
    }

    private fun setupSpinnersPersianas(productoView: View) {
        val spinnerTipo = productoView.findViewById<Spinner>(R.id.spinnerTipoPersiana)
        val spinnerColor = productoView.findViewById<Spinner>(R.id.spinnerColorPersiana)

        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposPersianas)
        adapterTipo.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerTipo.adapter = adapterTipo

        val adapterColor = ArrayAdapter(this, android.R.layout.simple_spinner_item, coloresDisponibles)
        adapterColor.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerColor.adapter = adapterColor

        // Listeners para recalcular precios
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calcularPrecios()
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calcularPrecios()
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // RadioGroup listeners
        val rgCenefa = productoView.findViewById<RadioGroup>(R.id.rgCenefaPersiana)
        val rgAccionamiento = productoView.findViewById<RadioGroup>(R.id.rgAccionamientoPersiana)

        rgCenefa.setOnCheckedChangeListener { _, _ -> calcularPrecios() }
        rgAccionamiento.setOnCheckedChangeListener { _, _ -> calcularPrecios() }
    }

    private fun setupSpinnersCortinas(productoView: View) {
        val spinnerTipo = productoView.findViewById<Spinner>(R.id.spinnerTipoCortina)
        val spinnerColor = productoView.findViewById<Spinner>(R.id.spinnerColorCortina)

        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposCortinas)
        adapterTipo.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerTipo.adapter = adapterTipo

        val adapterColor = ArrayAdapter(this, android.R.layout.simple_spinner_item, coloresDisponibles)
        adapterColor.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerColor.adapter = adapterColor

        // Listeners para recalcular precios
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calcularPrecios()
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calcularPrecios()
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // RadioGroup listeners
        val rgApertura = productoView.findViewById<RadioGroup>(R.id.rgAperturaCortina)
        val rgAccionamiento = productoView.findViewById<RadioGroup>(R.id.rgAccionamientoCortina)

        rgApertura.setOnCheckedChangeListener { _, _ -> calcularPrecios() }
        rgAccionamiento.setOnCheckedChangeListener { _, _ -> calcularPrecios() }
    }

    private fun setupCantidadButtons(productoView: View) {
        val btnMenos = productoView.findViewById<Button>(R.id.btnMenosCantidad)
        val btnMas = productoView.findViewById<Button>(R.id.btnMasCantidad)
        val tvCantidad = productoView.findViewById<TextView>(R.id.tvCantidad)

        btnMenos.setOnClickListener {
            val cantidadActual = tvCantidad.text.toString().toInt()
            if (cantidadActual > 1) {
                tvCantidad.text = (cantidadActual - 1).toString()
                calcularPrecios()
            }
        }

        btnMas.setOnClickListener {
            val cantidadActual = tvCantidad.text.toString().toInt()
            if (cantidadActual < 10) {
                tvCantidad.text = (cantidadActual + 1).toString()
                calcularPrecios()
            }
        }
    }

    private fun setupButtons() {
        btnAgregarProducto.setOnClickListener {
            agregarProducto()
        }

        btnGuardarCotizacion.setOnClickListener {
            guardarCotizacion()
        }

        btnCompartirCotizacion.setOnClickListener {
            compartirCotizacion()
        }

        btnLimpiarFormulario.setOnClickListener {
            limpiarFormulario()
        }

        btnVolver.setOnClickListener {
            volverAlMenu()
        }
    }

    private fun setupInstalacion() {
        rgUbicacionInstalacion.setOnCheckedChangeListener { _, _ ->
            calcularPrecios()
        }
    }

    private fun agregarProducto() {
        if (productosActivos < maxProductos) {
            productosActivos++

            when (productosActivos) {
                2 -> {
                    producto2.visibility = View.VISIBLE
                    setupProducto(producto2, 2)
                }
                3 -> {
                    producto3.visibility = View.VISIBLE
                    setupProducto(producto3, 3)
                }
                4 -> {
                    producto4.visibility = View.VISIBLE
                    setupProducto(producto4, 4)
                    btnAgregarProducto.visibility = View.GONE // Ocultar botón cuando llegue al máximo
                }
            }

            calcularPrecios()
        }
    }

    private fun eliminarProducto(productoView: View, numeroProducto: Int) {
        productoView.visibility = View.GONE
        limpiarProducto(productoView)

        // Reorganizar productos (mover los de abajo hacia arriba)
        reorganizarProductos()

        productosActivos--
        btnAgregarProducto.visibility = View.VISIBLE
        calcularPrecios()
    }

    private fun limpiarProducto(productoView: View) {
        // Resetear todos los campos del producto
        val spinnerTipo = productoView.findViewById<Spinner>(R.id.spinnerTipoProducto)
        spinnerTipo.setSelection(0)

        val etAncho = productoView.findViewById<EditText>(R.id.etAncho)
        val etAlto = productoView.findViewById<EditText>(R.id.etAlto)
        val tvCantidad = productoView.findViewById<TextView>(R.id.tvCantidad)

        etAncho.setText("")
        etAlto.setText("")
        tvCantidad.text = "1"

        // Ocultar layouts condicionales
        productoView.findViewById<LinearLayout>(R.id.layoutPersianas).visibility = View.GONE
        productoView.findViewById<LinearLayout>(R.id.layoutCortinas).visibility = View.GONE
        productoView.findViewById<LinearLayout>(R.id.layoutDimensiones).visibility = View.GONE
    }

    private fun reorganizarProductos() {
        // Simplificado: solo recontamos productos activos
        productosActivos = 0
        if (producto1.visibility == View.VISIBLE) productosActivos++
        if (producto2.visibility == View.VISIBLE) productosActivos++
        if (producto3.visibility == View.VISIBLE) productosActivos++
        if (producto4.visibility == View.VISIBLE) productosActivos++
    }

    private fun calcularPrecios() {
        var subtotal = 0.0

        // Calcular precio de cada producto visible
        val productos = listOf(producto1, producto2, producto3, producto4)

        productos.forEach { producto ->
            if (producto.visibility == View.VISIBLE) {
                val precioProducto = calcularPrecioProducto(producto)
                subtotal += precioProducto

                // Actualizar precio individual
                val tvPrecio = producto.findViewById<TextView>(R.id.tvPrecioProducto)
                tvPrecio.text = "Precio: $${String.format("%.2f", precioProducto)}"
            }
        }

        // Calcular instalación
        val costoInstalacion = when (rgUbicacionInstalacion.checkedRadioButtonId) {
            R.id.rbAfueras -> 25.0
            else -> 0.0
        }

        val total = subtotal + costoInstalacion

        // Actualizar UI
        tvSubtotal.text = "Subtotal: $${String.format("%.2f", subtotal)}"
        tvInstalacion.text = "Instalación: $${String.format("%.2f", costoInstalacion)}"
        tvTotal.text = "TOTAL: $${String.format("%.2f", total)}"
    }

    private fun calcularPrecioProducto(productoView: View): Double {
        val spinnerTipo = productoView.findViewById<Spinner>(R.id.spinnerTipoProducto)
        if (spinnerTipo.selectedItemPosition == 0) return 0.0

        val etAncho = productoView.findViewById<EditText>(R.id.etAncho)
        val etAlto = productoView.findViewById<EditText>(R.id.etAlto)
        val tvCantidad = productoView.findViewById<TextView>(R.id.tvCantidad)

        val ancho = etAncho.text.toString().toDoubleOrNull() ?: 0.0
        val alto = etAlto.text.toString().toDoubleOrNull() ?: 0.0
        val cantidad = tvCantidad.text.toString().toInt()

        if (ancho <= 0 || alto <= 0) return 0.0

        val area = ancho * alto
        var precioBase = 0.0

        // Precio base según tipo
        when (spinnerTipo.selectedItem.toString()) {
            "Persianas" -> {
                precioBase = area * 45.0 // $45 por m²

                // Verificar si es motorizada
                val rgAccionamiento = productoView.findViewById<RadioGroup>(R.id.rgAccionamientoPersiana)
                if (rgAccionamiento.checkedRadioButtonId == R.id.rbMotorizadaPersiana) {
                    precioBase *= 1.4 // +40%
                }
            }
            "Cortinas" -> {
                precioBase = area * 35.0 // $35 por m²

                // Verificar si es motorizada
                val rgAccionamiento = productoView.findViewById<RadioGroup>(R.id.rgAccionamientoCortina)
                if (rgAccionamiento.checkedRadioButtonId == R.id.rbMotorizadaCortina) {
                    precioBase *= 1.4 // +40%
                }
            }
        }

        return precioBase * cantidad
    }

    private fun guardarCotizacion() {
        val nombreCliente = etNombreCliente.text.toString().trim()

        if (nombreCliente.isEmpty()) {
            etNombreCliente.error = "El nombre del cliente es obligatorio"
            etNombreCliente.requestFocus()
            return
        }

        if (productosActivos == 0 || tvTotal.text.toString().contains("$0.00")) {
            Toast.makeText(this, "Debe agregar al menos un producto válido", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Guardar en SQLite
        val total = tvTotal.text.toString()
        val mensaje = "✅ Cotización guardada exitosamente!\n\nCliente: $nombreCliente\n$total"
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

        limpiarFormulario()
    }

    private fun compartirCotizacion() {
        val nombreCliente = etNombreCliente.text.toString().trim()
        val total = tvTotal.text.toString()

        if (nombreCliente.isEmpty() || total.contains("$0.00")) {
            Toast.makeText(this, "Complete la cotización antes de compartir", Toast.LENGTH_SHORT).show()
            return
        }

        val textoCotizacion = """
            🏠 COTIZACIÓN - MR. PERSIANA
            
            Cliente: $nombreCliente
            
            📊 Resumen:
            ${tvSubtotal.text}
            ${tvInstalacion.text}
            ═══════════════════
            ${tvTotal.text}
            
            📞 Contacto: Mr. Persiana
            ✅ Cotización válida por 30 días
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, textoCotizacion)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Cotización Mr. Persiana - $nombreCliente")

        startActivity(Intent.createChooser(intent, "Compartir cotización por:"))
    }

    private fun limpiarFormulario() {
        etNombreCliente.setText("")
        etObservaciones.setText("")
        spinnerCitaVinculada.setSelection(0)
        rgUbicacionInstalacion.clearCheck()

        // Limpiar todos los productos
        limpiarProducto(producto1)
        limpiarProducto(producto2)
        limpiarProducto(producto3)
        limpiarProducto(producto4)

        // Ocultar productos 2, 3, 4
        producto2.visibility = View.GONE
        producto3.visibility = View.GONE
        producto4.visibility = View.GONE

        productosActivos = 1
        btnAgregarProducto.visibility = View.VISIBLE

        calcularPrecios()
        Toast.makeText(this, "Formulario limpiado", Toast.LENGTH_SHORT).show()
    }

    private fun volverAlMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}