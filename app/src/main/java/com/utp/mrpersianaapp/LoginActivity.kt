package com.utp.mrpersianaapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    // Componentes de la interfaz
    private lateinit var spinnerTrabajadores: Spinner
    private lateinit var btnIngresar: Button
    private lateinit var tvInfo: TextView

    // SharedPreferences
    private lateinit var prefs: SharedPreferences

    // Lista de trabajadores de la empresa
    private val trabajadores = arrayOf(
        "Selecciona al trabajador...",
        "Jorge Izarra - Supervisor",
        "Anllelina Varcasia - Vendedora",
        "Christian Dutary - Instalador",
        "Yireikis Abrego - Vendedora"
    )

    // Variable para almacenar el trabajador seleccionado
    private var trabajadorSeleccionado: String = ""

    // Constantes para SharedPreferences
    companion object {
        const val PREFS_NAME = "MrPersianaPrefs"
        const val KEY_NOMBRE_TRABAJADOR = "nombre_trabajador"
        const val KEY_CARGO_TRABAJADOR = "cargo_trabajador"
        const val KEY_TRABAJADOR_COMPLETO = "trabajador_completo"
        const val KEY_PRIMERA_VEZ = "primera_vez"
        const val KEY_ULTIMA_SESION = "ultima_sesion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Inicializar componentes
        initializeComponents()

        // Verificar si hay datos guardados anteriormente
        verificarDatosGuardados()

        // Configurar spinner
        setupSpinner()

        // Configurar botón
        setupButton()
    }

    /**
     * Inicializar todos los componentes de la interfaz
     */
    private fun initializeComponents() {
        spinnerTrabajadores = findViewById(R.id.spinnerTrabajadores)
        btnIngresar = findViewById(R.id.btnIngresar)
        tvInfo = findViewById(R.id.tvInfo)
    }

    /**
     * Verificar si hay datos guardados de sesiones anteriores
     */
    private fun verificarDatosGuardados() {
        val esPrimeraVez = prefs.getBoolean(KEY_PRIMERA_VEZ, true)

        if (!esPrimeraVez) {
            // No es primera vez, mostrar información del último usuario
            val ultimoTrabajador = prefs.getString(KEY_TRABAJADOR_COMPLETO, "") ?: ""
            val ultimaSesion = prefs.getString(KEY_ULTIMA_SESION, "Nunca") ?: "Nunca"

            if (ultimoTrabajador.isNotEmpty()) {
                tvInfo.text = "Último usuario: ${ultimoTrabajador.split(" - ")[0]}\nÚltima sesión: $ultimaSesion\n\nSelecciona un trabajador para continuar"
            } else {
                tvInfo.text = "Selecciona un trabajador para continuar"
            }
        } else {
            tvInfo.text = "¡Bienvenido a Mr. Persiana!\nSelecciona un trabajador para comenzar"
        }
    }

    /**
     * Configurar el Spinner de trabajadores
     */
    private fun setupSpinner() {
        // Crear adaptador para el spinner con layout predeterminado
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            trabajadores
        )

        // Configurar el layout personalizado para el dropdown
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        // Asignar adaptador al spinner
        spinnerTrabajadores.adapter = adapter

        // Configurar listener para detectar selecciones
        spinnerTrabajadores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                handleWorkerSelection(position)
                // Aseguramos que el texto sea visible cambiando su color
                (view as? TextView)?.setTextColor(resources.getColor(R.color.negro_texto, null))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }

    /**
     * Manejar la selección de trabajador
     */
    private fun handleWorkerSelection(position: Int) {
        if (position == 0) {
            // Primera opción es el placeholder
            btnIngresar.isEnabled = false
            trabajadorSeleccionado = ""
        } else {
            // Trabajador válido seleccionado
            btnIngresar.isEnabled = true
            trabajadorSeleccionado = trabajadores[position]

            val nombre = trabajadorSeleccionado.split(" - ")[0]
            val cargo = trabajadorSeleccionado.split(" - ")[1]

            tvInfo.text = "Trabajador seleccionado: $nombre\nCargo: $cargo"
        }
    }

    /**
     * Configurar el botón de ingresar
     */
    private fun setupButton() {
        btnIngresar.setOnClickListener {
            ingresarAlSistema()
        }
    }

    /**
     * Navegar al menú principal y guardar datos en SharedPreferences
     */
    private fun ingresarAlSistema() {
        // Extraer datos del trabajador
        val nombre = trabajadorSeleccionado.split(" - ")[0]
        val cargo = trabajadorSeleccionado.split(" - ")[1]

        // Obtener fecha y hora actual
        val fechaActual = obtenerFechaHoraActual()

        // Guardar datos en SharedPreferences
        val editor = prefs.edit()
        editor.putString(KEY_NOMBRE_TRABAJADOR, nombre)
        editor.putString(KEY_CARGO_TRABAJADOR, cargo)
        editor.putString(KEY_TRABAJADOR_COMPLETO, trabajadorSeleccionado)
        editor.putBoolean(KEY_PRIMERA_VEZ, false)
        editor.putString(KEY_ULTIMA_SESION, fechaActual)
        editor.apply() // Usar apply() para operación asíncrona

        // Mostrar mensaje de bienvenida
        tvInfo.text = "Datos guardados\n¡Bienvenido $nombre!"

        // Pequeña pausa para que el usuario vea el mensaje
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            irAlMenu()
        }, 1000)
    }

    /**
     * Obtener fecha y hora actual formateada
     */
    private fun obtenerFechaHoraActual(): String {
        val calendar = java.util.Calendar.getInstance()
        return String.format(
            "%02d/%02d/%d %02d:%02d",
            calendar.get(java.util.Calendar.DAY_OF_MONTH),
            calendar.get(java.util.Calendar.MONTH) + 1,
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE)
        )
    }

    /**
     * Navegar al MenuActivity
     */
    private fun irAlMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish() // Finalizar LoginActivity para que no se pueda volver con el botón atrás
    }
}