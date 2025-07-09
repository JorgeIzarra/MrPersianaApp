# 🏠 Mr. Persiana App - Sistema de Cotización Digital

**Aplicación móvil Android para la gestión y cotización de persianas y cortinas desarrollada en Kotlin.**

![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)

## 📱 Descripción

Mr. Persiana App es una solución digital completa que digitaliza el proceso tradicional de cotización de persianas y cortinas, transformando un proceso manual con papeles sueltos en un sistema organizado, profesional y eficiente.

### 🎯 Problema que Resuelve

- **Proceso manual desorganizado** → Sistema digital estructurado
- **Papeles sueltos** → Base de datos persistente
- **Sin seguimiento** → Historial completo de actividades
- **Imagen poco profesional** → Cotizaciones profesionales compartibles
- **Cálculos manuales** → Cálculo automático de precios

## ✨ Características Principales

### 👥 **Gestión de Trabajadores**
- Sistema de login con selección de trabajador
- Persistencia de datos con SharedPreferences
- Información del usuario en todas las pantallas

### 📅 **Gestión de Citas**
- Crear y programar visitas a clientes
- Validación de fechas futuras
- Información completa del cliente y ubicación
- Estados: Pendiente → Completada → Cotizada

### 📋 **Sistema de Cotización Avanzado**
- **Productos múltiples**: Hasta 4 productos por cotización
- **Tipos disponibles**: Persianas y Cortinas
- **Especificaciones detalladas**:
  - Dimensiones personalizadas
  - Tipos específicos (Screen, Blackout, Velo)
  - 8 colores disponibles
  - Opciones de cenefa (persianas)
  - Apertura central/lateral (cortinas)
  - Accionamiento manual/motorizado (+40% precio)
- **Cálculo automático de precios**
- **Cotizaciones vinculadas** a citas existentes

### 📊 **Historial Unificado**
- Lista combinada de citas y cotizaciones
- **Filtros avanzados**:
  - Por tipo (Solo Citas / Solo Cotizaciones)
  - Por nombre de cliente
  - Por fecha (Hoy / Esta semana)
- **Ordenamiento**: Por fecha, nombre A-Z/Z-A
- Contador de resultados en tiempo real

### 🔗 **Integración con Apps Externas**
- **Google Maps**: Abrir ubicaciones de clientes
- **Calendario**: Acceso directo al calendario del dispositivo
- **Compartir**: WhatsApp, Email, SMS con formato profesional

### 💾 **Persistencia de Datos**
- **SQLite** con 3 tablas relacionadas:
  - `Citas`: Información de visitas programadas
  - `Cotizaciones`: Cotizaciones generadas
  - `Productos`: Productos individuales por cotización
- **CRUD completo** para todas las entidades
- **Relaciones de integridad** entre tablas

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Framework**: Android SDK (Material Design 3)
- **Base de Datos**: SQLite con SQLiteOpenHelper
- **Almacenamiento**: SharedPreferences
- **Navegación**: Intent explícitos e implícitos
- **UI**: Material Design Components
- **Arquitectura**: MVVM con Repository Pattern

## 📁 Estructura del Proyecto

```
app/src/main/
├── java/com/utp/mrpersianaapp/
│   ├── data/                      # Data classes y DatabaseHelper
│   │   ├── Cita.kt               # Modelo de datos para citas
│   │   ├── Cotizacion.kt         # Modelo de datos para cotizaciones
│   │   ├── Producto.kt           # Modelo de datos para productos
│   │   └── DatabaseHelper.kt     # Gestión de SQLite
│   ├── LoginActivity.kt          # Pantalla de inicio de sesión
│   ├── MenuActivity.kt           # Menú principal
│   ├── CrearCitaActivity.kt      # Formulario de citas
│   ├── CrearCotizacionActivity.kt # Formulario de cotizaciones
│   ├── ListaUnificadaActivity.kt # Historial y filtros
│   └── DetalleActivity.kt        # Vista detallada
├── res/
│   ├── layout/                   # Layouts XML
│   ├── values/
│   │   ├── colors.xml           # Paleta de colores corporativos
│   │   └── themes.xml           # Temas Material Design
│   └── drawable/                # Recursos gráficos
└── AndroidManifest.xml
```

## 🚀 Instalación y Configuración

### Requisitos Previos
- Android Studio Arctic Fox (2020.3.1) o superior
- Kotlin 1.5.0 o superior
- SDK mínimo: API 21 (Android 5.0)
- SDK objetivo: API 34 (Android 14)

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/JorgeIzarra/MrPersianaApp.git
   cd MrPersianaApp
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar la carpeta del proyecto
   - Esperar a que Gradle sincronice

3. **Configurar emulador o dispositivo**
   - Crear AVD con API 21+ o conectar dispositivo físico
   - Habilitar "Modo desarrollador" y "Depuración USB"

4. **Ejecutar la aplicación**
   ```bash
   ./gradlew assembleDebug
   # O usar el botón Run en Android Studio
   ```

## 📖 Uso de la Aplicación

### 1. **Inicio de Sesión**
- Seleccionar trabajador de la lista
- Los datos se guardan automáticamente para futuras sesiones

### 2. **Crear Cita**
- Completar información del cliente
- Seleccionar fecha y hora con DatePicker/TimePicker
- Especificar tipo de consulta
- Agregar notas adicionales

### 3. **Crear Cotización**
- Vincular a cita existente (opcional)
- Agregar hasta 4 productos diferentes
- Configurar especificaciones detalladas
- Seleccionar ubicación de instalación
- Cálculo automático de precios

### 4. **Ver Historial**
- Filtrar por tipo o cliente
- Ordenar por fecha o nombre
- Ver detalles completos
- Compartir cotizaciones

## 📐 Arquitectura y Patrones

### **Modelo de Datos**
```kotlin
// Ejemplo de relación entre entidades
Cita (1) ←→ (0..n) Cotizacion (1) ←→ (1..4) Producto
```

### **Flujo de Navegación**
```
LoginActivity → MenuActivity ↓
                ├── CrearCitaActivity → DetalleActivity
                ├── CrearCotizacionActivity → DetalleActivity  
                └── ListaUnificadaActivity → DetalleActivity
```

### **Intent Patterns**
- **Explícitos**: Navegación entre Activities propias
- **Implícitos**: Integración con apps del sistema (Maps, Calendar, Share)

## 🎨 Diseño y UX

### **Paleta de Colores Corporativa**
- **Azul Principal**: `#4A90BD` - Botones y elementos principales
- **Azul Oscuro**: `#2C5282` - Headers y status bar
- **Naranja Persiana**: `#D69E2E` - Acentos y elementos secundarios
- **Gris Claro**: `#E2EDF5` - Fondos y separadores

### **Principios de Diseño**
- **Material Design 3**: Componentes modernos y consistentes
- **Accesibilidad**: Contraste adecuado y navegación clara
- **Responsive**: Adaptable a diferentes tamaños de pantalla
- **Intuitividad**: Flujo natural y predecible

## 📊 Base de Datos

### **Esquema SQLite**

#### Tabla `citas`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PK | Identificador único |
| nombre_cliente | TEXT | Nombre completo del cliente |
| telefono | TEXT | Número de contacto |
| direccion | TEXT | Dirección completa |
| fecha_visita | TEXT | Fecha programada (DD/MM/YYYY) |
| hora_visita | TEXT | Hora programada (HH:MM AM/PM) |
| tipo_consulta | TEXT | Tipo de servicio solicitado |
| estado | TEXT | PENDIENTE/COMPLETADA/COTIZADA |

#### Tabla `cotizaciones`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PK | Identificador único |
| cita_id | INTEGER FK | Referencia a cita (opcional) |
| nombre_cliente | TEXT | Cliente de la cotización |
| ubicacion_instalacion | TEXT | Lugar de instalación |
| subtotal | REAL | Suma de productos |
| costo_instalacion | REAL | Costo adicional de traslado |
| total | REAL | Precio final |
| estado | TEXT | ENVIADA/SEGUIMIENTO/CERRADA |

#### Tabla `productos`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PK | Identificador único |
| cotizacion_id | INTEGER FK | Referencia a cotización |
| tipo_producto | TEXT | Persianas/Cortinas |
| subtipo | TEXT | Screen/Blackout/Velo |
| color | TEXT | Color seleccionado |
| ancho | REAL | Ancho en metros |
| alto | REAL | Alto en metros |
| cantidad | INTEGER | Número de unidades |
| precio_total | REAL | Precio calculado |
| especificaciones | BOOLEAN | Cenefa, motorizada, apertura |

## 🔧 Funcionalidades Técnicas

### **Cálculo de Precios**
```kotlin
// Lógica de cálculo implementada
val area = ancho * alto
var precioBase = area * tarifaPorM2
if (esMotorizada) precioBase *= 1.4 // +40%
val precioTotal = precioBase * cantidad + costoInstalacion
```

### **Persistencia de Datos**
- **SharedPreferences**: Datos de sesión del trabajador
- **SQLite**: Datos de negocio (citas, cotizaciones, productos)
- **Transacciones**: Integridad en operaciones complejas

### **Validaciones Implementadas**
- Campos obligatorios en formularios
- Fechas futuras para citas
- Dimensiones válidas para productos
- Integridad referencial en base de datos

## 🤝 Contribución

### **Para Contribuir**
1. Fork el proyecto
2. Crear branch para feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

### **Estándares de Código**
- Seguir convenciones de Kotlin
- Documentar funciones complejas
- Mantener arquitectura MVVM
- Escribir tests unitarios

## 📝 Documentación Académica

**Proyecto desarrollado para:**
- **Curso**: Desarrollo de Software VI
- **Institución**: Universidad Tecnológica de Panamá
- **Profesor**: Giovani Sánchez
- **Año**: 2025

### **Requisitos Académicos Cumplidos**
- ✅ Navegación entre Activities (Intent explícitos/implícitos)
- ✅ Manejo de datos persistentes (SharedPreferences + SQLite)
- ✅ Interfaz funcional con Material Design
- ✅ Integración con aplicaciones del sistema
- ✅ Base de datos relacional con 3+ tablas
- ✅ Operaciones CRUD completas
- ✅ Validaciones y manejo de errores

## 📞 Soporte

Para reportar bugs o solicitar features:
- **Issues**: [GitHub Issues](https://github.com/JorgeIzarra/MrPersianaApp/issues)
- **Email**: jorge.izarra.dev@gmail.com

---

**Mr. Persiana App** - Digitalizando el futuro de las cotizaciones 🏠✨
