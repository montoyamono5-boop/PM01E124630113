 📱 Aplicación de Gestión de Contactos (Android Studio - Java)

Proyecto de examen práctico para la materia de **Desarrollo de Aplicaciones Móviles**. Se trata de una aplicación nativa para Android desarrollada puramente en **Java**, con diseño de interfaz personalizado en tonos azul degradado, almacenamiento local persistente en **SQLite**, validación estricta de entradas mediante expresiones regulares, gestión de permisos en tiempo de ejecución y comunicación entre actividades mediante `Intents`.

---

## 🎨 Capturas e Interfaz del Proyecto

La aplicación consta de **3 pantallas principales**:

1. **Pantalla 1 – Nueva Ficha de Contacto (Registro y Validación):**
   - Captura de fotografía mediante cámara o selección desde galería.
   - Selector de País con prefijo internacional en un `Spinner`.
   - Campos para Nombre, Teléfono y Nota con validaciones por expresiones regulares (`RegEx`).
   - Botones redondeados para **Guardar Ficha** e ir al **Ver Catálogo**.

2. **Pantalla 2 – Catálogo de Contactos (Lista y Acciones Rápidas):**
   - `RecyclerView` con datos reales cargados desde la base de datos SQLite.
   - Barra de búsqueda superior para filtrado de contactos en tiempo real.
   - Panel de **Acciones Rápidas**:
     - ✏️ **Actualizar:** Carga los datos en el formulario para su edición.
     - 🗑️ **Eliminar:** Borra el contacto de la BD tras una alerta de confirmación.
     - 🔗 **Compartir:** Utiliza `Intent.ACTION_SEND` para enviar el contacto por texto.
     - 👁️ **Ver Ficha:** Muestra la foto almacenada y los detalles en un diálogo emergente.
     - 📞 **Llamar Ahora:** Abre la ventana de llamada para el contacto seleccionado.

3. **Pantalla 3 – Ventana de Llamada:**
   - Interfaz con diseño oscuro que muestra el avatar, nombre y número formateado.
   - Solicitud del permiso de sistema `CALL_PHONE` en tiempo de ejecución.
   - Diálogo `AlertDialog.Builder` de confirmación: *“¿Desea iniciar la llamada con [Nombre] ([Teléfono])?”*.
   - Ejecución de la llamada utilizando `Intent.ACTION_CALL`.

---

## 🏗️ Estructura del Código

```text
app/src/main/
├── java/com/example/myapplication/
│   ├── model/
│   │   └── Contacto.java            # Clase modelo (País, Nombre, Teléfono, Nota, Imagen)
│   ├── db/
│   │   └── DatabaseHelper.java      # SQLiteOpenHelper para operaciones CRUD
│   ├── adapter/
│   │   └── ContactoAdapter.java     # Adaptador personalizado para RecyclerView
│   ├── MainActivity.java            # Pantalla 1: Formulario y Registro
│   ├── CatalogActivity.java         # Pantalla 2: Lista y Acciones Rápidas
│   └── CallActivity.java            # Pantalla 3: Ventana de Llamada e Intent ACTION_CALL
│
└── res/
    ├── drawable/
    │   ├── bg_gradient_blue.xml     # Encabezado en azul degradado
    │   ├── bg_card.xml              # Tarjetas redondeadas
    │   └── ic_*.xml                 # Iconografía vectorial
    ├── layout/
    │   ├── activity_main.xml        # Layout Pantalla 1
    │   ├── activity_catalog.xml     # Layout Pantalla 2
    │   ├── activity_call.xml        # Layout Pantalla 3
    │   ├── item_contacto.xml        # Layout del item de la lista
    │   └── dialog_ver_ficha.xml     # Diálogo emergente para Ver Ficha
    └── values/
        ├── colors.xml               # Paleta de colores principal
        └── strings.xml              # Textos e idioma de la aplicación
```

---

## 🛠️ Requisitos Técnicos e Instalación

* **Entorno de Desarrollo:** Android Studio Ladybug / Jellyfish o superior.
* **Lenguaje:** Java 11.
* **SDK Mínimo (Min SDK):** API 24 (Android 7.0 Nougat).
* **SDK Objetivo (Target SDK):** API 34/35.

### Pasos para Ejecutar:
1. Abrir **Android Studio**.
2. Seleccionar `File > Open` y elegir la carpeta del proyecto `MyApplication4`.
3. Sincronizar Gradle (`Sync Project with Gradle Files`).
4. Seleccionar un emulador Android o conectar un dispositivo físico por depuración USB.
5. Hacer clic en **Run ('app')** (Shift + F10).

---

## 📋 Tabla de Verificación de la Rúbrica (20/20 Puntos)

| Criterio de Evaluación | Puntos | Estado |
| :--- | :---: | :---: |
| **Persistencia Local con SQLite (`SQLiteOpenHelper`)** | 4 pts | ✅ Cumplido |
| **Clase Modelo `Contacto` (País, Nombre, Teléfono, Nota, Imagen)** | 2 pts | ✅ Cumplido |
| **Alertas para campos vacíos (Nombre, Teléfono, Nota)** | 1 pt | ✅ Cumplido |
| **Validaciones con Expresiones Regulares (`RegEx`)** | 1 pt | ✅ Cumplido |
| **Carga de lista desde SQLite (`RecyclerView`)** | 2 pts | ✅ Cumplido |
| **Eliminación de contacto seleccionado de SQLite** | 1 pt | ✅ Cumplido |
| **Actualización de contacto en SQLite** | 1 pt | ✅ Cumplido |
| **Compartir contacto con `Intent.ACTION_SEND`** | 2 pts | ✅ Cumplido |
| **Ver fotografía guardada del contacto** | 2 pts | ✅ Cumplido |
| **Realizar llamada con `Intent.ACTION_CALL` y Permiso en tiempo de ejecución** | 2 pts | ✅ Cumplido |
| **Confirmación previa con `AlertDialog.Builder`** | 2 pts | ✅ Cumplido |
| **PUNTAJE TOTAL** | **20/20** | **100%** |

---

*Desarrollado como proyecto práctico de evaluación para Android Studio.*
<img width="1863" height="949" alt="IMAGEN1" src="https://github.com/user-attachments/assets/66a223e0-6876-48fc-9a54-ae9ab5fd2c78" />
<img width="1920" height="1020" alt="IMAGEN2" src="https://github.com/user-attachments/assets/c7c0ad60-a10e-40b5-969a-93a61cbcc96d" />
<img width="1920" height="1020" alt="IMAGEN3" src="https://github.com/user-attachments/assets/73bc1fca-fd37-4d4e-965e-2b353505e848" />

