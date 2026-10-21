# Pa'lante

Aplicación Android hecha con Jetpack Compose para monitorear la actividad física durante un recorrido y guardar fotos de los puntos de interés que se encuentran en el camino.

## Funcionalidades

- **Permisos**: al pulsar "Iniciar Recorrido" se piden los permisos de ubicación (GPS), cámara y actividad física.
- **Pasos y distancia**: se usa el sensor `TYPE_STEP_COUNTER`. Si el teléfono no lo tiene, se cuentan los pasos con el acelerómetro. La distancia se calcula como pasos × 0.75 m.
- **Modo claro / oscuro automático**: el sensor de luz (`TYPE_LIGHT`) cambia el tema de la app y el estilo del mapa según la iluminación.
- **Google Maps**: muestra la ubicación actual del usuario y la sigue durante el recorrido.
- **Cámara**: con el botón "Tomar foto" se abre la cámara. Cada foto guarda las coordenadas, la fecha y la hora, y aparece un marcador en el mapa.
- **Puntos de Interés**: un dashboard con el resumen del recorrido y una cuadrícula con todas las fotos y sus datos. Las fotos se guardan en la caché de la app (`cacheDir/fotos`).
- **Ruta**: el camino recorrido se dibuja en el mapa con una Polyline.

## Estructura

```
app/src/main/java/com/example/recorrido/
├── MainActivity.kt           // tema de la app
├── navigation/               // Navigation 3 (NavDisplay + back stack)
├── RecorridoViewModel.kt     // estado del recorrido
├── PuntoInteres.kt
├── sensores/
│   ├── SensorPasos.kt
│   ├── SensorLuz.kt
│   └── RastreadorUbicacion.kt
└── ui/
    ├── pantallas/            // Inicio, Recorrido (mapa), Puntos (dashboard)
    └── theme/
```

## Cómo ejecutarlo

1. Clonar el repositorio y abrirlo en Android Studio.
2. Conseguir una API key de Google Maps (Google Cloud Console → habilitar *Maps SDK for Android*).
3. Agregar la key en el archivo `local.properties` (este archivo no se sube al repositorio):
   ```
   MAPS_API_KEY=tu_api_key
   ```
4. Ejecutar la app en un dispositivo físico (el emulador no tiene sensor de pasos ni de luz reales).
