package com.utp.mrpersianaapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Información de la base de datos
        private const val DATABASE_NAME = "mr_persiana.db"
        private const val DATABASE_VERSION = 1

        // Tabla CITAS
        private const val TABLE_CITAS = "citas"
        private const val CITA_ID = "id"
        private const val CITA_NOMBRE_CLIENTE = "nombre_cliente"
        private const val CITA_TELEFONO = "telefono"
        private const val CITA_DIRECCION = "direccion"
        private const val CITA_FECHA_VISITA = "fecha_visita"
        private const val CITA_HORA_VISITA = "hora_visita"
        private const val CITA_TIPO_CONSULTA = "tipo_consulta"
        private const val CITA_NOTAS = "notas_adicionales"
        private const val CITA_ESTADO = "estado"
        private const val CITA_FECHA_CREACION = "fecha_creacion"

        // Tabla COTIZACIONES
        private const val TABLE_COTIZACIONES = "cotizaciones"
        private const val COT_ID = "id"
        private const val COT_CITA_ID = "cita_id"
        private const val COT_NOMBRE_CLIENTE = "nombre_cliente"
        private const val COT_UBICACION = "ubicacion_instalacion"
        private const val COT_OBSERVACIONES = "observaciones"
        private const val COT_SUBTOTAL = "subtotal"
        private const val COT_COSTO_INSTALACION = "costo_instalacion"
        private const val COT_TOTAL = "total"
        private const val COT_ESTADO = "estado"
        private const val COT_FECHA_CREACION = "fecha_creacion"

        // Tabla PRODUCTOS
        private const val TABLE_PRODUCTOS = "productos"
        private const val PROD_ID = "id"
        private const val PROD_COTIZACION_ID = "cotizacion_id"
        private const val PROD_TIPO = "tipo_producto"
        private const val PROD_SUBTIPO = "subtipo"
        private const val PROD_COLOR = "color"
        private const val PROD_ANCHO = "ancho"
        private const val PROD_ALTO = "alto"
        private const val PROD_CANTIDAD = "cantidad"
        private const val PROD_PRECIO_UNITARIO = "precio_unitario"
        private const val PROD_PRECIO_TOTAL = "precio_total"
        private const val PROD_TIENE_CENEFA = "tiene_cenefa"
        private const val PROD_ES_MOTORIZADA = "es_motorizada"
        private const val PROD_APERTURA_CENTRAL = "apertura_central"
        private const val PROD_FECHA_CREACION = "fecha_creacion"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla CITAS
        val createCitasTable = """
            CREATE TABLE $TABLE_CITAS (
                $CITA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $CITA_NOMBRE_CLIENTE TEXT NOT NULL,
                $CITA_TELEFONO TEXT NOT NULL,
                $CITA_DIRECCION TEXT NOT NULL,
                $CITA_FECHA_VISITA TEXT NOT NULL,
                $CITA_HORA_VISITA TEXT NOT NULL,
                $CITA_TIPO_CONSULTA TEXT NOT NULL,
                $CITA_NOTAS TEXT,
                $CITA_ESTADO TEXT DEFAULT 'PENDIENTE',
                $CITA_FECHA_CREACION TEXT NOT NULL
            )
        """.trimIndent()

        // Crear tabla COTIZACIONES
        val createCotizacionesTable = """
            CREATE TABLE $TABLE_COTIZACIONES (
                $COT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COT_CITA_ID INTEGER,
                $COT_NOMBRE_CLIENTE TEXT NOT NULL,
                $COT_UBICACION TEXT NOT NULL,
                $COT_OBSERVACIONES TEXT,
                $COT_SUBTOTAL REAL NOT NULL,
                $COT_COSTO_INSTALACION REAL NOT NULL,
                $COT_TOTAL REAL NOT NULL,
                $COT_ESTADO TEXT DEFAULT 'ENVIADA',
                $COT_FECHA_CREACION TEXT NOT NULL,
                FOREIGN KEY ($COT_CITA_ID) REFERENCES $TABLE_CITAS($CITA_ID)
            )
        """.trimIndent()

        // Crear tabla PRODUCTOS
        val createProductosTable = """
            CREATE TABLE $TABLE_PRODUCTOS (
                $PROD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $PROD_COTIZACION_ID INTEGER NOT NULL,
                $PROD_TIPO TEXT NOT NULL,
                $PROD_SUBTIPO TEXT,
                $PROD_COLOR TEXT,
                $PROD_ANCHO REAL NOT NULL,
                $PROD_ALTO REAL NOT NULL,
                $PROD_CANTIDAD INTEGER NOT NULL,
                $PROD_PRECIO_UNITARIO REAL NOT NULL,
                $PROD_PRECIO_TOTAL REAL NOT NULL,
                $PROD_TIENE_CENEFA BOOLEAN DEFAULT 0,
                $PROD_ES_MOTORIZADA BOOLEAN DEFAULT 0,
                $PROD_APERTURA_CENTRAL BOOLEAN DEFAULT 1,
                $PROD_FECHA_CREACION TEXT NOT NULL,
                FOREIGN KEY ($PROD_COTIZACION_ID) REFERENCES $TABLE_COTIZACIONES($COT_ID)
            )
        """.trimIndent()

        // Ejecutar creación de tablas
        db.execSQL(createCitasTable)
        db.execSQL(createCotizacionesTable)
        db.execSQL(createProductosTable)

        println("📊 Base de datos creada exitosamente")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // En caso de actualización, eliminar tablas existentes y recrear
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COTIZACIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CITAS")
        onCreate(db)
        println("📊 Base de datos actualizada de versión $oldVersion a $newVersion")
    }

    // ==================== MÉTODOS CRUD PARA CITAS ====================

    /**
     * Insertar una nueva cita
     */
    fun insertarCita(cita: Cita): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(CITA_NOMBRE_CLIENTE, cita.nombreCliente)
            put(CITA_TELEFONO, cita.telefono)
            put(CITA_DIRECCION, cita.direccion)
            put(CITA_FECHA_VISITA, cita.fechaVisita)
            put(CITA_HORA_VISITA, cita.horaVisita)
            put(CITA_TIPO_CONSULTA, cita.tipoConsulta)
            put(CITA_NOTAS, cita.notasAdicionales)
            put(CITA_ESTADO, cita.estado)
            put(CITA_FECHA_CREACION, cita.fechaCreacion)
        }

        val id = db.insert(TABLE_CITAS, null, values)
        db.close()

        println("📅 Cita insertada con ID: $id")
        return id
    }

    /**
     * Obtener todas las citas
     */
    fun obtenerTodasLasCitas(): List<Cita> {
        val citas = mutableListOf<Cita>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CITAS ORDER BY $CITA_FECHA_CREACION DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val cita = Cita(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(CITA_ID)),
                    nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow(CITA_NOMBRE_CLIENTE)),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow(CITA_TELEFONO)),
                    direccion = cursor.getString(cursor.getColumnIndexOrThrow(CITA_DIRECCION)),
                    fechaVisita = cursor.getString(cursor.getColumnIndexOrThrow(CITA_FECHA_VISITA)),
                    horaVisita = cursor.getString(cursor.getColumnIndexOrThrow(CITA_HORA_VISITA)),
                    tipoConsulta = cursor.getString(cursor.getColumnIndexOrThrow(CITA_TIPO_CONSULTA)),
                    notasAdicionales = cursor.getString(cursor.getColumnIndexOrThrow(CITA_NOTAS)),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow(CITA_ESTADO)),
                    fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow(CITA_FECHA_CREACION))
                )
                citas.add(cita)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        println("📅 Obtenidas ${citas.size} citas de la base de datos")
        return citas
    }

    /**
     * Obtener cita por ID
     */
    fun obtenerCitaPorId(id: Long): Cita? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CITAS WHERE $CITA_ID = ?", arrayOf(id.toString()))

        var cita: Cita? = null
        if (cursor.moveToFirst()) {
            cita = Cita(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(CITA_ID)),
                nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow(CITA_NOMBRE_CLIENTE)),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow(CITA_TELEFONO)),
                direccion = cursor.getString(cursor.getColumnIndexOrThrow(CITA_DIRECCION)),
                fechaVisita = cursor.getString(cursor.getColumnIndexOrThrow(CITA_FECHA_VISITA)),
                horaVisita = cursor.getString(cursor.getColumnIndexOrThrow(CITA_HORA_VISITA)),
                tipoConsulta = cursor.getString(cursor.getColumnIndexOrThrow(CITA_TIPO_CONSULTA)),
                notasAdicionales = cursor.getString(cursor.getColumnIndexOrThrow(CITA_NOTAS)),
                estado = cursor.getString(cursor.getColumnIndexOrThrow(CITA_ESTADO)),
                fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow(CITA_FECHA_CREACION))
            )
        }

        cursor.close()
        db.close()
        return cita
    }

    /**
     * Actualizar estado de una cita
     */
    fun actualizarEstadoCita(id: Long, nuevoEstado: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(CITA_ESTADO, nuevoEstado)
        }

        val filasAfectadas = db.update(TABLE_CITAS, values, "$CITA_ID = ?", arrayOf(id.toString()))
        db.close()

        println("📅 Estado de cita $id actualizado a: $nuevoEstado")
        return filasAfectadas
    }

    // ==================== MÉTODOS CRUD PARA COTIZACIONES ====================

    /**
     * Insertar una nueva cotización
     */
    fun insertarCotizacion(cotizacion: Cotizacion): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COT_CITA_ID, cotizacion.citaId)
            put(COT_NOMBRE_CLIENTE, cotizacion.nombreCliente)
            put(COT_UBICACION, cotizacion.ubicacionInstalacion)
            put(COT_OBSERVACIONES, cotizacion.observaciones)
            put(COT_SUBTOTAL, cotizacion.subtotal)
            put(COT_COSTO_INSTALACION, cotizacion.costoInstalacion)
            put(COT_TOTAL, cotizacion.total)
            put(COT_ESTADO, cotizacion.estado)
            put(COT_FECHA_CREACION, cotizacion.fechaCreacion)
        }

        val id = db.insert(TABLE_COTIZACIONES, null, values)
        db.close()

        println("📋 Cotización insertada con ID: $id")
        return id
    }

    /**
     * Obtener todas las cotizaciones
     */
    fun obtenerTodasLasCotizaciones(): List<Cotizacion> {
        val cotizaciones = mutableListOf<Cotizacion>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_COTIZACIONES ORDER BY $COT_FECHA_CREACION DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val cotizacionId = cursor.getLong(cursor.getColumnIndexOrThrow(COT_ID))

                val cotizacion = Cotizacion(
                    id = cotizacionId,
                    citaId = if (cursor.isNull(cursor.getColumnIndexOrThrow(COT_CITA_ID))) null
                    else cursor.getLong(cursor.getColumnIndexOrThrow(COT_CITA_ID)),
                    nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow(COT_NOMBRE_CLIENTE)),
                    ubicacionInstalacion = cursor.getString(cursor.getColumnIndexOrThrow(COT_UBICACION)),
                    observaciones = cursor.getString(cursor.getColumnIndexOrThrow(COT_OBSERVACIONES)),
                    subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow(COT_SUBTOTAL)),
                    costoInstalacion = cursor.getDouble(cursor.getColumnIndexOrThrow(COT_COSTO_INSTALACION)),
                    total = cursor.getDouble(cursor.getColumnIndexOrThrow(COT_TOTAL)),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow(COT_ESTADO)),
                    fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow(COT_FECHA_CREACION)),
                    productos = obtenerProductosPorCotizacion(cotizacionId)
                )
                cotizaciones.add(cotizacion)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        println("📋 Obtenidas ${cotizaciones.size} cotizaciones de la base de datos")
        return cotizaciones
    }

    // ==================== MÉTODOS CRUD PARA PRODUCTOS ====================

    /**
     * Insertar un nuevo producto
     */
    fun insertarProducto(producto: Producto): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(PROD_COTIZACION_ID, producto.cotizacionId)
            put(PROD_TIPO, producto.tipoProducto)
            put(PROD_SUBTIPO, producto.subtipo)
            put(PROD_COLOR, producto.color)
            put(PROD_ANCHO, producto.ancho)
            put(PROD_ALTO, producto.alto)
            put(PROD_CANTIDAD, producto.cantidad)
            put(PROD_PRECIO_UNITARIO, producto.precioUnitario)
            put(PROD_PRECIO_TOTAL, producto.precioTotal)
            put(PROD_TIENE_CENEFA, if (producto.tieneCenefa) 1 else 0)
            put(PROD_ES_MOTORIZADA, if (producto.esMotorizada) 1 else 0)
            put(PROD_APERTURA_CENTRAL, if (producto.aperturaCentral) 1 else 0)
            put(PROD_FECHA_CREACION, producto.fechaCreacion)
        }

        val id = db.insert(TABLE_PRODUCTOS, null, values)
        db.close()

        println("🏷️ Producto insertado con ID: $id")
        return id
    }

    /**
     * Obtener productos por cotización
     */
    fun obtenerProductosPorCotizacion(cotizacionId: Long): List<Producto> {
        val productos = mutableListOf<Producto>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_PRODUCTOS WHERE $PROD_COTIZACION_ID = ? ORDER BY $PROD_FECHA_CREACION",
            arrayOf(cotizacionId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val producto = Producto(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(PROD_ID)),
                    cotizacionId = cursor.getLong(cursor.getColumnIndexOrThrow(PROD_COTIZACION_ID)),
                    tipoProducto = cursor.getString(cursor.getColumnIndexOrThrow(PROD_TIPO)),
                    subtipo = cursor.getString(cursor.getColumnIndexOrThrow(PROD_SUBTIPO)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(PROD_COLOR)),
                    ancho = cursor.getDouble(cursor.getColumnIndexOrThrow(PROD_ANCHO)),
                    alto = cursor.getDouble(cursor.getColumnIndexOrThrow(PROD_ALTO)),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(PROD_CANTIDAD)),
                    precioUnitario = cursor.getDouble(cursor.getColumnIndexOrThrow(PROD_PRECIO_UNITARIO)),
                    precioTotal = cursor.getDouble(cursor.getColumnIndexOrThrow(PROD_PRECIO_TOTAL)),
                    tieneCenefa = cursor.getInt(cursor.getColumnIndexOrThrow(PROD_TIENE_CENEFA)) == 1,
                    esMotorizada = cursor.getInt(cursor.getColumnIndexOrThrow(PROD_ES_MOTORIZADA)) == 1,
                    aperturaCentral = cursor.getInt(cursor.getColumnIndexOrThrow(PROD_APERTURA_CENTRAL)) == 1,
                    fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow(PROD_FECHA_CREACION))
                )
                productos.add(producto)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return productos
    }

    /**
     * Función auxiliar para obtener fecha actual
     */
    fun obtenerFechaActual(): String {
        val calendar = Calendar.getInstance()
        return String.format(
            "%02d/%02d/%d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)
        )
    }
}