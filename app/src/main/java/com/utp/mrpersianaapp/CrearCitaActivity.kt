package com.utp.mrpersianaapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class CrearCitaActivity : AppCompatActivity() {

    // Componentes de la interfaz
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cita)

        // Inicializar componentes
        initializeComponents()

        // Configurar spinner
        setupSpinner()

        // Configurar campos de fecha y hora
        setupDateTimeFields()

        // Configurar botones
        setupButtons()
    }

    /**
     * Inicializar todos los componentes de la interfaz
     */
    private fun initializeComponents() {
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
     * Configurar el spinner de tipo de consulta
     */
    private fun setupSpinner() {
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
                (view as? android.widget.TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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
     * Mostrar selector de fecha
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
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

        // No permitir fechas pasadas
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    /**
     * Mostrar selector de hora
     */
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
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
            hour, minute, false // false para formato 12 horas
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
     * Validar y guardar la cita
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

        // TODO: Aquí se guardará en SQLite después
        // Por ahora solo mostrar confirmación

        val mensajeExito = "✅ Cita guardada exitosamente!\n\n" +
                "Cliente: $nombreCliente\n" +
                "Fecha: $fechaVisita a las $horaVisita\n" +
                "Tipo: $tipoConsultaSeleccionado"

        Toast.makeText(this, mensajeExito, Toast.LENGTH_LONG).show()

        // Limpiar formulario después de guardar
        limpiarFormulario()

        // Opcional: volver al menú después de un delay
        // Handler(Looper.getMainLooper()).postDelayed({ volverAlMenu() }, 2000)
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
        etNombreCliente.setText("")
        etTelefono.setText("")
        etDireccion.setText("")
        etFechaVisita.setText("")
        etHoraVisita.setText("")
        etNotasAdicionales.setText("")
        spinnerTipoConsulta.setSelection(0)
        tipoConsultaSeleccionado = ""

        // Limpiar errores
        etNombreCliente.error = null
        etTelefono.error = null
        etDireccion.error = null

        Toast.makeText(this, "Formulario limpiado", Toast.LENGTH_SHORT).show()
    }

    /**
     * Volver al MenuActivity
     */
    private fun volverAlMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

//    /**
//     * Manejar el botón atrás del dispositivo
//     */
//    override fun onBackPressed() {
//        volverAlMenu()
//    }
}