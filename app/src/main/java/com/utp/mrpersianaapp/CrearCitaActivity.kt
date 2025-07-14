package com.utp.mrpersianaapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.utp.mrpersianaapp.data.Cita
import com.utp.mrpersianaapp.data.DatabaseHelper
import com.utp.mrpersianaapp.data.DatabaseHelper.ClienteUnico  // ← ESTE ES EL IMPORT CLAVE
import java.util.Calendar
import android.view.ContextThemeWrapper

class CrearCitaActivity : AppCompatActivity() {

    // Componentes de la interfaz
    private lateinit var scrollView: ScrollView
    private lateinit var rgTipoCliente: RadioGroup
    private lateinit var rbNuevoCliente: RadioButton
    private lateinit var rbClienteExistente: RadioButton
    private lateinit var layoutClienteExistente: LinearLayout
    private lateinit var spinnerClienteExistente: Spinner
    private lateinit var etNombreCliente: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etFechaVisita: EditText
    private lateinit var etHoraVisita: EditText
    private lateinit var etNotasAdicionales: EditText
    private lateinit var spinnerTipoConsulta: Spinner
    private lateinit var btnGuardarCita: Button
    private lateinit var btnLimpiarFormulario: Button
    private lateinit var btnVolver: Button

    // Base de datos
    private lateinit var databaseHelper: DatabaseHelper

    // Variables para clientes existentes - USANDO LA DATA CLASS DEL DATABASEHELPER
    private var clientesUnicos = mutableListOf<ClienteUnico>()
    private var clientesParaSpinner = mutableListOf<String>()

    // Opciones para el spinner de tipo de consulta
    private val tiposConsulta = arrayOf(
        "Selecciona el tipo de consulta...",
        "Persianas solamente",
        "Cortinas solamente",
        "Persianas y Cortinas",
        "Consultoría general",
        "Reparación/Mantenimiento"
    )

    // Variables para almacenar datos
    private var tipoConsultaSeleccionado: String = ""

    // ❌ ELIMINAR ESTA DATA CLASS - YA NO LA NECESITAMOS
    // data class ClienteUnico(
    //     val nombre: String,
    //     val telefono: String,
    //     val direccion: String
    // )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cita)

        // Inicializar base de datos
        databaseHelper = DatabaseHelper(this)

        // Inicializar componentes
        initializeComponents()

        // Cargar clientes existentes
        cargarClientesUnicos()

        // Configurar RadioGroup
        setupRadioGroup()

        // Configurar spinners
        setupSpinners()

        // Configurar campos de fecha y hora
        setupDateTimeFields()

        // Configurar botones
        setupButtons()

        // Configurar scroll automático para el campo de notas
        setupNotasScrollListener()
    }

    /**
     * Inicializar todos los componentes de la interfaz
     */
    private fun initializeComponents() {
        scrollView = findViewById(R.id.scrollViewCrearCita)
        rgTipoCliente = findViewById(R.id.rgTipoCliente)
        rbNuevoCliente = findViewById(R.id.rbNuevoCliente)
        rbClienteExistente = findViewById(R.id.rbClienteExistente)
        layoutClienteExistente = findViewById(R.id.layoutClienteExistente)
        spinnerClienteExistente = findViewById(R.id.spinnerClienteExistente)
        etNombreCliente = findViewById(R.id.etNombreCliente)
        etTelefono = findViewById(R.id.etTelefono)
        etDireccion = findViewById(R.id.etDireccion)
        etFechaVisita = findViewById(R.id.etFechaVisita)
        etHoraVisita = findViewById(R.id.etHoraVisita)
        etNotasAdicionales = findViewById(R.id.etNotasAdicionales)
        spinnerTipoConsulta = findViewById(R.id.spinnerTipoConsulta)
        btnGuardarCita = findViewById(R.id.btnGuardarCita)
        btnLimpiarFormulario = findViewById(R.id.btnLimpiarFormulario)
        btnVolver = findViewById(R.id.btnVolver)
    }

    /**
     * Cargar clientes únicos desde la base de datos
     */
    private fun cargarClientesUnicos() {
        try {
            // AHORA FUNCIONA PORQUE USAMOS EL MISMO TIPO DE DATA CLASS
            val clientesUnicosObtenidos = databaseHelper.obtenerClientesUnicos()
            clientesUnicos.clear()
            clientesUnicos.addAll(clientesUnicosObtenidos)

            // Crear lista para el spinner
            clientesParaSpinner.clear()
            clientesParaSpinner.add("Selecciona un cliente...")

            clientesUnicos.forEach { cliente ->
                val formatoCliente = "${cliente.nombre} - ${cliente.telefono}"
                clientesParaSpinner.add(formatoCliente)
            }

            println("👤 Clientes únicos cargados: ${clientesUnicos.size}")

        } catch (e: Exception) {
            println("❌ Error al cargar clientes únicos: ${e.message}")
            // Fallback en caso de error
            clientesParaSpinner.clear()
            clientesParaSpinner.add("No hay clientes registrados")
        }
    }

    /**
     * Configurar el RadioGroup para seleccionar tipo de cliente
     */
    private fun setupRadioGroup() {
        rgTipoCliente.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbNuevoCliente -> {
                    // Mostrar campos editables para nuevo cliente
                    layoutClienteExistente.visibility = View.GONE
                    habilitarCamposCliente(true)
                    limpiarCamposCliente()
                }
                R.id.rbClienteExistente -> {
                    // Mostrar spinner para cliente existente
                    layoutClienteExistente.visibility = View.VISIBLE
                    habilitarCamposCliente(false)
                }
            }
        }
    }

    /**
     * Configurar todos los spinners
     */
    private fun setupSpinners() {
        // Spinner de clientes existentes
        setupSpinnerClientes()

        // Spinner de tipo de consulta (existente)
        setupSpinnerTipoConsulta()
    }

    /**
     * Configurar spinner de clientes existentes
     */
    private fun setupSpinnerClientes() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            clientesParaSpinner
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerClienteExistente.adapter = adapter

        spinnerClienteExistente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0 && position <= clientesUnicos.size) {
                    // Auto-rellenar campos con datos del cliente seleccionado
                    val clienteSeleccionado = clientesUnicos[position - 1]
                    autoRellenarCamposCliente(clienteSeleccionado)
                }
                // Cambiar color del texto para visibilidad
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Configurar el spinner de tipo de consulta (método existente mejorado)
     */
    private fun setupSpinnerTipoConsulta() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tiposConsulta
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerTipoConsulta.adapter = adapter

        spinnerTipoConsulta.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    tipoConsultaSeleccionado = ""
                } else {
                    tipoConsultaSeleccionado = tiposConsulta[position]
                }
                // Aseguramos que el texto sea visible cambiando su color
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Auto-rellenar campos con datos del cliente seleccionado
     */
    private fun autoRellenarCamposCliente(cliente: ClienteUnico) {
        etNombreCliente.setText(cliente.nombre)
        etTelefono.setText(cliente.telefono)
        etDireccion.setText(cliente.direccion)

        // Mostrar mensaje informativo
        Toast.makeText(this, "✅ Datos del cliente cargados: ${cliente.nombre}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Habilitar/deshabilitar campos de cliente
     */
    private fun habilitarCamposCliente(habilitar: Boolean) {
        etNombreCliente.isEnabled = habilitar
        etTelefono.isEnabled = habilitar
        etDireccion.isEnabled = habilitar

        // Cambiar apariencia visual
        val alpha = if (habilitar) 1.0f else 0.6f
        etNombreCliente.alpha = alpha
        etTelefono.alpha = alpha
        etDireccion.alpha = alpha

        // Cambiar hint según el estado
        if (!habilitar) {
            etNombreCliente.hint = "Se llenará automáticamente..."
            etTelefono.hint = "Se llenará automáticamente..."
            etDireccion.hint = "Se llenará automáticamente..."
        } else {
            etNombreCliente.hint = "Ingrese el nombre completo"
            etTelefono.hint = "Ej: 6123-4567"
            etDireccion.hint = "Calle, casa/edificio, sector, ciudad"
        }
    }

    /**
     * Limpiar campos de cliente
     */
    private fun limpiarCamposCliente() {
        etNombreCliente.setText("")
        etTelefono.setText("")
        etDireccion.setText("")
        etNombreCliente.error = null
        etTelefono.error = null
        etDireccion.error = null
    }

    /**
     * Configurar campos de fecha y hora con pickers
     */
    private fun setupDateTimeFields() {
        // Configurar campo de fecha
        etFechaVisita.setOnClickListener {
            showDatePicker()
        }

        // Configurar campo de hora
        etHoraVisita.setOnClickListener {
            showTimePicker()
        }
    }

    /**
     * Mostrar selector de fecha CON TEMA MEJORADO
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            ContextThemeWrapper(this, R.style.PickerTheme), // ← USAR ContextThemeWrapper
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%02d/%02d/%d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )
                etFechaVisita.setText(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    /**
     * Mostrar selector de hora CON TEMA MEJORADO
     */
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            ContextThemeWrapper(this, R.style.PickerTheme), // ← USAR ContextThemeWrapper
            { _, selectedHour, selectedMinute ->
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val hour12 = if (selectedHour > 12) selectedHour - 12 else if (selectedHour == 0) 12 else selectedHour

                val formattedTime = String.format(
                    "%d:%02d %s",
                    hour12,
                    selectedMinute,
                    amPm
                )
                etHoraVisita.setText(formattedTime)
            },
            hour, minute, false
        )

        timePickerDialog.show()
    }

    /**
     * Configurar todos los botones
     */
    private fun setupButtons() {
        btnGuardarCita.setOnClickListener {
            guardarCita()
        }

        btnLimpiarFormulario.setOnClickListener {
            limpiarFormulario()
        }

        btnVolver.setOnClickListener {
            volverAlMenu()
        }
    }

    /**
     * Validar y guardar la cita en la base de datos
     */
    private fun guardarCita() {
        // Obtener datos del formulario
        val nombreCliente = etNombreCliente.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val fechaVisita = etFechaVisita.text.toString().trim()
        val horaVisita = etHoraVisita.text.toString().trim()
        val notasAdicionales = etNotasAdicionales.text.toString().trim()

        // Validar campos obligatorios
        if (!validarCamposObligatorios(nombreCliente, telefono, direccion, fechaVisita, horaVisita)) {
            return
        }

        try {
            // Crear objeto Cita
            val cita = Cita(
                nombreCliente = nombreCliente,
                telefono = telefono,
                direccion = direccion,
                fechaVisita = fechaVisita,
                horaVisita = horaVisita,
                tipoConsulta = tipoConsultaSeleccionado,
                notasAdicionales = notasAdicionales.ifEmpty { null },
                estado = Cita.ESTADO_PENDIENTE,
                fechaCreacion = obtenerFechaActual()
            )

            // Guardar en base de datos
            val citaId = databaseHelper.insertarCita(cita)

            if (citaId > 0) {
                // Éxito al guardar
                val mensajeExito = "✅ Cita guardada exitosamente!\n\n" +
                        "ID: $citaId\n" +
                        "Cliente: $nombreCliente\n" +
                        "Fecha: $fechaVisita a las $horaVisita\n" +
                        "Tipo: $tipoConsultaSeleccionado"

                Toast.makeText(this, mensajeExito, Toast.LENGTH_LONG).show()

                // Limpiar formulario después de guardar
                limpiarFormulario()

                // Opcional: navegar a detalle de la cita
                navegarADetalleCita(citaId)
            } else {
                // Error al guardar
                Toast.makeText(this, "❌ Error al guardar la cita. Inténtalo de nuevo.", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            // Manejo de errores
            println("❌ Error al guardar cita: ${e.message}")
            Toast.makeText(this, "❌ Error inesperado al guardar la cita.", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Navegar al detalle de la cita recién creada
     */
    private fun navegarADetalleCita(citaId: Long) {
        // Opcional: navegar al detalle después de un delay
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                // Obtener la cita recién guardada de la BD
                val cita = databaseHelper.obtenerCitaPorId(citaId)

                if (cita != null) {
                    // Crear bundle para DetalleActivity
                    val bundle = Bundle().apply {
                        putString("nombre_cliente", cita.nombreCliente)
                        putString("fecha_creacion", cita.fechaCreacion)
                        putString("estado", cita.estado)
                        putString("telefono", cita.telefono)
                        putString("direccion", cita.direccion)
                        putString("fecha_visita", cita.fechaVisita)
                        putString("hora_visita", cita.horaVisita)
                        putString("tipo_consulta", cita.tipoConsulta)
                        putString("notas_adicionales", cita.notasAdicionales ?: "Sin notas adicionales")
                    }

                    val intent = Intent(this, DetalleActivity::class.java)
                    intent.putExtra("tipo_detalle", "CITA")
                    intent.putExtra("datos", bundle)

                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                println("❌ Error al navegar al detalle: ${e.message}")
                // Si hay error, solo volver al menú
                volverAlMenu()
            }
        }, 2000)
    }

    /**
     * Validar que todos los campos obligatorios estén llenos
     */
    private fun validarCamposObligatorios(
        nombre: String,
        telefono: String,
        direccion: String,
        fecha: String,
        hora: String
    ): Boolean {

        when {
            nombre.isEmpty() -> {
                etNombreCliente.error = "El nombre del cliente es obligatorio"
                etNombreCliente.requestFocus()
                return false
            }
            telefono.isEmpty() -> {
                etTelefono.error = "El teléfono es obligatorio"
                etTelefono.requestFocus()
                return false
            }
            direccion.isEmpty() -> {
                etDireccion.error = "La dirección es obligatoria"
                etDireccion.requestFocus()
                return false
            }
            fecha.isEmpty() -> {
                Toast.makeText(this, "Debe seleccionar una fecha de visita", Toast.LENGTH_SHORT).show()
                return false
            }
            hora.isEmpty() -> {
                Toast.makeText(this, "Debe seleccionar una hora de visita", Toast.LENGTH_SHORT).show()
                return false
            }
            tipoConsultaSeleccionado.isEmpty() -> {
                Toast.makeText(this, "Debe seleccionar el tipo de consulta", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }

    /**
     * Limpiar todos los campos del formulario
     */
    private fun limpiarFormulario() {
        // Resetear RadioGroup a nuevo cliente
        rbNuevoCliente.isChecked = true
        layoutClienteExistente.visibility = View.GONE
        spinnerClienteExistente.setSelection(0)

        // Habilitar campos y limpiar
        habilitarCamposCliente(true)
        limpiarCamposCliente()

        // Limpiar otros campos
        etFechaVisita.setText("")
        etHoraVisita.setText("")
        etNotasAdicionales.setText("")
        spinnerTipoConsulta.setSelection(0)
        tipoConsultaSeleccionado = ""

        Toast.makeText(this, "Formulario limpiado", Toast.LENGTH_SHORT).show()
    }

    /**
     * Configurar scroll automático para el campo de notas
     */
    private fun setupNotasScrollListener() {
        etNotasAdicionales.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Esperar a que aparezca el teclado y luego hacer scroll
                Handler(Looper.getMainLooper()).postDelayed({
                    scrollToField(etNotasAdicionales)
                }, 300) // Delay para dar tiempo al teclado
            }
        }

        // También manejar cuando se hace clic en el campo
        etNotasAdicionales.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                scrollToField(etNotasAdicionales)
            }, 300)
        }
    }

    /**
     * Hacer scroll automático a un campo específico
     */
    private fun scrollToField(field: EditText) {
        val location = IntArray(2)
        field.getLocationOnScreen(location)

        // Calcular la posición ideal para el scroll
        val fieldY = location[1]
        val screenHeight = resources.displayMetrics.heightPixels
        val keyboardHeight = screenHeight / 3 // Estimación del teclado

        // Si el campo está muy abajo, hacer scroll
        if (fieldY > screenHeight - keyboardHeight - 200) {
            val scrollY = fieldY - (screenHeight - keyboardHeight - 300)
            scrollView.smoothScrollTo(0, scrollY)
        }
    }

    /**
     * Obtener fecha actual formateada
     */
    private fun obtenerFechaActual(): String {
        val calendar = Calendar.getInstance()
        return String.format(
            "%02d/%02d/%d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
    }

    /**
     * Volver al MenuActivity
     */
    private fun volverAlMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}