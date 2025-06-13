package com.utp.mrpersianaapp

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar componentes
        initializeComponents()

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
            tvInfo.text = "Selecciona un trabajador para continuar"
            trabajadorSeleccionado = ""
        } else {
            // Trabajador válido seleccionado
            btnIngresar.isEnabled = true
            trabajadorSeleccionado = trabajadores[position]
            tvInfo.text = "Trabajador seleccionado: ${trabajadorSeleccionado.split(" - ")[0]}"
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
     * Navegar al menú principal
     */
    private fun ingresarAlSistema() {
        // Crear intent explícito para ir al MenuActivity
        val intent = Intent(this, MenuActivity::class.java)

        // Enviar datos del trabajador seleccionado usando putExtra
        intent.putExtra("trabajador_nombre", trabajadorSeleccionado.split(" - ")[0])
        intent.putExtra("trabajador_cargo", trabajadorSeleccionado.split(" - ")[1])
        intent.putExtra("trabajador_completo", trabajadorSeleccionado)

        // Iniciar la nueva actividad
        startActivity(intent)

        // Finalizar LoginActivity para que no se pueda volver con el botón atrás
        finish()
    }
}