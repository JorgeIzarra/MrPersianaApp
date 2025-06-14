package com.utp.mrpersianaapp

import android.content.Intent
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

    // Datos del trabajador recibidos del Login
    private var nombreTrabajador: String = ""
    private var cargoTrabajador: String = ""
    private var trabajadorCompleto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Inicializar componentes
        initializeComponents()

        // Recibir datos del LoginActivity
        receiveWorkerData()

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
     * Recibir datos del trabajador desde LoginActivity
     */
    private fun receiveWorkerData() {
        // Recibir datos enviados por putExtra desde LoginActivity
        nombreTrabajador = intent.getStringExtra("trabajador_nombre") ?: "Usuario"
        cargoTrabajador = intent.getStringExtra("trabajador_cargo") ?: "Sin cargo"
        trabajadorCompleto = intent.getStringExtra("trabajador_completo") ?: "Usuario - Sin cargo"
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
            // TODO: Navegar a ListaUnificadaActivity
            Toast.makeText(this, "Ver Historial - Próximamente", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this, "Sesión cerrada. ¡Hasta luego ${nombreTrabajador}!", Toast.LENGTH_SHORT).show()

        // Crear intent para volver al LoginActivity
        val intent = Intent(this, LoginActivity::class.java)

        // Limpiar el stack de actividades para que no se pueda volver atrás
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

//    /**
//     * Manejar el botón atrás - cerrar sesión
//     */
//    override fun onBackPressed() {
//        // En lugar de volver atrás, cerrar sesión
//        cerrarSesion()
//    }
}