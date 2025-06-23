package com.utp.mrpersianaapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    // Componentes de la interfaz
    private lateinit var tvNombreTrabajador: TextView
    private lateinit var tvCargoTrabajador: TextView
    private lateinit var btnCrearCita: Button
    private lateinit var btnCrearCotizacion: Button
    private lateinit var btnVerHistorial: Button
    private lateinit var btnCalendario: Button
    private lateinit var btnCerrarSesion: Button

    // SharedPreferences
    private lateinit var prefs: SharedPreferences

    // Datos del trabajador
    private var nombreTrabajador: String = ""
    private var cargoTrabajador: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Inicializar SharedPreferences
        prefs = getSharedPreferences("MrPersianaPrefs", Context.MODE_PRIVATE)

        // Inicializar componentes
        initializeComponents()

        // Cargar datos del trabajador desde SharedPreferences
        loadWorkerDataFromPreferences()

        // Configurar información del trabajador
        setupWorkerInfo()

        // Configurar botones
        setupButtons()
    }

    /**
     * Inicializar todos los componentes de la interfaz
     */
    private fun initializeComponents() {
        tvNombreTrabajador = findViewById(R.id.tvNombreTrabajador)
        tvCargoTrabajador = findViewById(R.id.tvCargoTrabajador)
        btnCrearCita = findViewById(R.id.btnCrearCita)
        btnCrearCotizacion = findViewById(R.id.btnCrearCotizacion)
        btnVerHistorial = findViewById(R.id.btnVerHistorial)
        btnCalendario = findViewById(R.id.btnCalendario)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
    }

    /**
     * Cargar datos del trabajador desde SharedPreferences
     */
    private fun loadWorkerDataFromPreferences() {
        // Leer datos guardados en SharedPreferences
        nombreTrabajador = prefs.getString("nombre_trabajador", "Usuario") ?: "Usuario"
        cargoTrabajador = prefs.getString("cargo_trabajador", "Sin cargo") ?: "Sin cargo"

        // Debug: Mostrar en consola que se cargaron los datos
        println("📱 MenuActivity - Datos cargados desde SharedPreferences:")
        println("   Nombre: $nombreTrabajador")
        println("   Cargo: $cargoTrabajador")
    }

    /**
     * Configurar la información del trabajador en la interfaz
     */
    private fun setupWorkerInfo() {
        tvNombreTrabajador.text = nombreTrabajador
        tvCargoTrabajador.text = cargoTrabajador
    }

    /**
     * Configurar todos los botones del menú
     */
    private fun setupButtons() {
        // Botón Crear Cita
        btnCrearCita.setOnClickListener {
            val intent = Intent(this, CrearCitaActivity::class.java)
            startActivity(intent)
        }

        // Botón Crear Cotización
        btnCrearCotizacion.setOnClickListener {
            val intent = Intent(this, CrearCotizacionActivity::class.java)
            startActivity(intent)
        }

        // Botón Ver Historial
        btnVerHistorial.setOnClickListener {
            val intent = Intent(this, ListaUnificadaActivity::class.java)
            startActivity(intent)
        }

        // Botón Calendario (Intent implícito)
        btnCalendario.setOnClickListener {
            abrirCalendario()
        }

        // Botón Cerrar Sesión
        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    /**
     * Abrir el calendario del dispositivo usando Intent implícito
     */
    private fun abrirCalendario() {
        try {
            // Intent implícito para abrir el calendario
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("content://com.android.calendar/time")
            startActivity(intent)
        } catch (e: Exception) {
            // Si no se puede abrir el calendario, mostrar mensaje
            Toast.makeText(this, "No se pudo abrir el calendario", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Cerrar sesión y volver al LoginActivity
     */
    private fun cerrarSesion() {
        // Mostrar mensaje de confirmación
        Toast.makeText(this, "Sesión cerrada. ¡Hasta luego $nombreTrabajador!", Toast.LENGTH_SHORT).show()

        // OPCIONAL: Limpiar algunos datos de sesión en SharedPreferences
        // (pero mantenemos los datos del usuario para la próxima vez)
        val editor = prefs.edit()
        editor.putString("ultima_sesion", obtenerFechaHoraActual())
        editor.apply()

        // Crear intent para volver al LoginActivity
        val intent = Intent(this, LoginActivity::class.java)

        // Limpiar el stack de actividades para que no se pueda volver atrás
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
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

//    /**
//     * Manejar el botón atrás - cerrar sesión
//     */
//    override fun onBackPressed() {
//        // En lugar de volver atrás, cerrar sesión
//        cerrarSesion()
//    }
}