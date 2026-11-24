# Gestor Personal de Tareas


**Gestor Personal de Tareas** es una aplicación nativa para Android desarrollada en Kotlin y Jetpack Compose. Permite gestionar tareas personales de forma local con persistencia reactiva en base de datos, filtrar tareas pendientes mediante preferencias persistentes, importar sugerencias desde una API REST externa y compartir tareas con otras aplicaciones instaladas en el dispositivo.

## Funcionalidades implementadas

* **CRUD completo de tareas:** Creación, visualización en listado reactivo, edición, marcado como completada y eliminación (con diálogo de confirmación) persistidos en SQLite mediante Room.

* **Validación de formulario:** Comprobación de título obligatorio al guardar, mensaje de error claro y limpieza automática del error al escribir, junto con selector de prioridad (`Alta`, `Media`, `Baja`).

* **Filtrado persistente:** Interruptor (*Switch*) dedicado encima del listado para ocultar tareas completadas, cuyo estado se guarda entre sesiones mediante Jetpack DataStore y alterna entre consultas SQL específicas en el DAO.

* **Importación desde API externa:** Consumo del endpoint `/todos` de *JSONPlaceholder* mediante Retrofit, limitando a 20 sugerencias, con gestión de estados de red (*Cargando*, *Éxito*, *Error con botón Reintentar*) e importación directa a Room.

* **Compartir tareas:** Envío de los datos estructurados de una tarea (título, descripción, prioridad y estado) en texto plano mediante `Intent.ACTION_SEND` y el selector nativo del sistema (`Intent.createChooser`).

## Organización básica del proyecto

```
com.example.gestorpersonaldetareas
├── data
│   ├── local
│   │   ├── AppDatabase.kt        # Instancia Singleton de la base de datos Room
│   │   ├── Tarea.kt              # Entidad @Entity de la tabla "tareas"
│   │   └── TareaDao.kt           # Consultas SQL (@Query) y operaciones CRUD
│   └── preferences
│       └── PreferenciasManager.kt # Lectura y escritura asíncrona con Jetpack DataStore
├── model
│   └── Prioridad.kt              # Enum con los niveles de prioridad (Alta, Media, Baja)
├── network
│   ├── ApiService.kt             # Interfaz de peticiones HTTP con Retrofit
│   ├── RetrofitClient.kt         # Cliente HTTP Singleton con GsonConverterFactory
│   └── TareaRemotaDto.kt         # Modelo de datos remoto (DTO) y conversión a entidad local
├── ui
│   ├── FormularioTarea.kt        # Formulario de creación y edición con validación
│   ├── ItemTarea.kt              # Componente de tarjeta individual con menú contextual
│   ├── PantallaPrincipal.kt      # Navegación, barra de filtro DataStore y listado principal
│   ├── PantallaTareasEjemplo.kt  # Pantalla de sugerencias desde API y estados de red
│   └── theme                     # Tema, paleta de colores y tipografía Material 3
└── MainActivity.kt               # Punto de entrada de la app e integración de Intents


```

## Bibliotecas utilizadas

* **Jetpack Compose & Material 3:** Construcción de la interfaz gráfica declarativa (`Scaffold`, `TopAppBar`, `LazyColumn`, `ExposedDropdownMenuBox`, `Switch`, `AlertDialog`).

* **Room (`2.7.1`) + KSP:** Persistencia local en SQLite mediante `@Entity`, `@Dao`, `@Database` y flujos reactivos (`Flow`).

* **Jetpack DataStore (Preferences):** Almacenamiento asíncrono clave-valor (`booleanPreferencesKey`) para conservar el estado del filtro de tareas completadas.

* **Retrofit 2 + Converter Gson:** Cliente REST para el consumo de la API externa (`JSONPlaceholder`) y deserialización automática de JSON a objetos Kotlin.

* **Kotlin Coroutines & Flow:** Gestión de operaciones asíncronas en segundo plano (`suspend`, `rememberCoroutineScope`, `LaunchedEffect`, `collectAsState`).

## Instrucciones para ejecutar la aplicación

### Opción A: Compilación y ejecución desde Android Studio (Código fuente)

1. Clonar o abrir el proyecto en **Android Studio** (con soporte para AGP 9.0+ y Kotlin 2.x).

2. Esperar a que **Gradle** descargue todas las dependencias y sincronice el proyecto (*Sync Project with Gradle Files*).

3. Seleccionar un emulador o dispositivo físico con **Android 8.0 (API 26) o superior** y conexión a Internet.

4. Pulsar el botón **Run** (`Shift + F10`) para compilar e instalar la aplicación.

### Opción B: Instalación directa mediante archivo `.apk`

1. Descargar o transferir el archivo `app-debug.apk` (o el `.apk` de entrega) a un dispositivo físico o emulador con **Android 8.0 (API 26) o superior**.

2. **En dispositivo físico:**
   * Abrir el archivo `.apk` desde el gestor de archivos del teléfono o tablet.
   * Si el sistema lo solicita, conceder permiso para **instalar aplicaciones desconocidas** (*Ajustes > Seguridad* o desde el aviso emergente del explorador de archivos).
   * Pulsar **Instalar** y, una vez finalizada la instalación, pulsar **Abrir**.

3. **En emulador de Android Studio:**
   * Arrastrar y soltar el archivo `.apk` directamente sobre la pantalla del emulador en ejecución (o ejecutar `adb install ruta/al/archivo.apk` desde la terminal) y abrir el icono **Gestor Personal de Tareas** en el cajón de aplicaciones.

4. Asegurarse de disponer de conexión a Internet si se desea probar la importación de sugerencias desde la API externa.

## Configuraciones de los dos emuladores utilizados

### Emulador 1: Medium Phone API 37.0

* **Dispositivo:** Generic Medium Phone (`medium_phone`)

* **Sistema operativo / API:** Android 17 (API 37.0) — Google Play (`arm64-v8a`)

* **Pantalla y densidad:** 1080 × 2400 px (`420 dpi` / xxhdpi), orientación vertical (`portrait`)

* **Memoria RAM y Heap:** 2048 MB RAM (`336 MB` VM Heap)

* **Almacenamiento:** Partición de datos de `10 GB` + tarjeta SD habilitada

* **CPU y Gráficos:** 1 núcleo (`hw.cpu.ncore = 1`), aceleración gráfica por hardware (`auto`)

* **Red:** Velocidad completa (`full`), sin latencia simulada (`none`)

### Emulador 2: Medium Tablet (API 37.2)

* **Dispositivo:** Generic Medium Tablet (`medium_tablet`)

* **Sistema operativo / API:** Android 17 (API 37.2) — Google APIs PlayStore (`arm64-v8a`, páginas de 16 KB)

* **Pantalla y densidad:** 2560 × 1600 px (`320 dpi` / xhdpi), orientación horizontal (`landscape`)

* **Memoria RAM y Heap:** 1907 MB RAM (`192 MB` VM Heap)

* **Almacenamiento:** Partición de datos de `10 GB` + tarjeta SD habilitada

* **CPU y Gráficos:** 4 núcleos (`hw.cpu.ncore = 4`), aceleración gráfica por hardware (`auto`)

* **Red:** Velocidad completa (`full`), sin latencia simulada (`none`)

## Tabla de pruebas realizadas y resultados obtenidos

| ID | Caso de prueba | Pasos ejecutados | Resultado esperado | Estado | 
| ----- | ----- | ----- | ----- | ----- | 
| **P01** | Validación de formulario | Pulsar *Guardar* con el título vacío y luego escribir texto. | Muestra mensaje de error, impide guardar y limpia el aviso al escribir. | ✅ Superada | 
| **P02** | Ciclo CRUD y persistencia Room | Crear, editar, marcar como completada y reiniciar la app. | Los cambios se reflejan en tiempo real y persisten tras cerrar la app. | ✅ Superada | 
| **P03** | Confirmación de borrado | Pulsar *Eliminar* en una tarea y probar *Cancelar* y *Eliminar*. | El diálogo solicita confirmación antes de borrar definitivamente de SQLite. | ✅ Superada | 
| **P04** | Filtro persistente (DataStore) | Activar *Ocultar completadas*, cerrar la app y volver a abrirla. | Solo muestra pendientes y conserva el estado del `Switch` entre sesiones. | ✅ Superada | 
| **P05** | Importación desde API (Retrofit) | Abrir *Sugerencias* con red activa e importar una tarea. | Lista 20 sugerencias, guarda la elegida en Room y vuelve al listado. | ✅ Superada | 
| **P06** | Gestión de error de red | Activar *Modo Avión*, entrar en *Sugerencias* y luego pulsar *Reintentar*. | Muestra aviso de error sin cerrar la app y recupera datos al volver la red. | ✅ Superada | 
| **P07** | Compartir tarea (`Intent`) | Pulsar *Compartir* desde el menú contextual de una tarjeta. | Abre el *Sharesheet* nativo con los datos formateados en texto plano. | ✅ Superada | 
| **P08** | Adaptabilidad Multi-dispositivo | Ejecutar en *Medium Phone* (vertical) y *Medium Tablet* (horizontal). | La interfaz Material 3 se adapta correctamente sin solapamientos. | ✅ Superada | 

## Capturas de pantalla

### 1. Listado principal de tareas

<img width="463" height="740" alt="imagen" src="https://github.com/user-attachments/assets/1928c37b-9112-41e3-ba7d-9ae0eddeae48" />

### 2. Formulario de creación / edición de tarea

<img width="462" height="737" alt="imagen" src="https://github.com/user-attachments/assets/95fc7fc9-f395-46bf-bfac-3d058da6f28d" />

### 3. Consulta a la API externa (Sugerencias)

<img width="459" height="736" alt="imagen" src="https://github.com/user-attachments/assets/e4a8913d-b208-4ede-b8fe-0719eed83402" />

### 4.Compartir tarea mediante Intent nativo

<img width="461" height="738" alt="imagen" src="https://github.com/user-attachments/assets/707312f5-5b2b-4725-a853-03f5abbd1f71" />


## Enlace al chat de IA usado durante el desarrollo

[Ver historial completo del chat con Gemini (PDF)](https://drive.google.com/file/d/11owEpgkU9i3vTk2OP3f7zOkRaTxSwb2w/view?usp=sharing)


Puede que el enlace compartido no funcione (https://gemini.google.com/share/d/1RcnZjYjJShR1_-Gx3qdZWgUPldvXG3pA?usp=sharing)

## Incidencias conocidas o funcionalidades que no se hayan podido completar

* **Problemas de compilación ocurridos durante el desarrollo (resueltos):**

  * **Incompatibilidad de versiones entre Room, KSP y Kotlin 2.x:** Durante la configuración inicial de la capa de persistencia local, Gradle arrojó errores de compilación y generación de código al procesar las anotaciones de Room (`@Database` y `@Dao`). Se solucionó actualizando Room a la versión `2.7.1` y alineando la versión del plugin KSP en el archivo `libs.versions.toml` con la versión del compilador de Kotlin del proyecto.

  * **Errores de compilación en sentencias `when` exhaustivas sobre `sealed interface`:** Al añadir nuevos estados de navegación (`Pantalla.TareasEjemplo`) y estados de red (`EstadoTareasEjemplo`), el compilador bloqueó la compilación al exigir cobertura total de ramas en los bloques `when` de la interfaz gráfica. Se resolvió completando todas las ramas correspondientes en `PantallaPrincipal` y `PantallaTareasEjemplo`.

  * **Resolución de referencias e importaciones entre paquetes:** Durante la modularización del proyecto (`data.local`, `data.preferences`, `network`, `model`, `ui`), surgieron errores de compilación por referencias no resueltas (como el mapeo de `TareaRemotaDto` a `Tarea` o el acceso a las propiedades de `Prioridad`), corregidos importando explícitamente los modelos y funciones de extensión entre capas.

* **Incidencias de interfaz detectadas y resueltas:**

  * **Solapamiento de barras superiores (`TopAppBar`):** Al integrar `PantallaTareasEjemplo` dentro de `PantallaPrincipal`, se producía una doble barra superior al coexistir dos componentes `Scaffold`. Se corrigió aislando el flujo de renderizado condicional para que cada pantalla gestione su propio `Scaffold`.

* **Incidencias pendientes:**

  * No quedan incidencias bloqueantes ni funcionalidades principales sin completar. Como detalle menor pendiente de mejora futura, las tareas importadas desde la API externa se guardan inicialmente con una descripción genérica predefinida hasta que el usuario las edita manualmente.
