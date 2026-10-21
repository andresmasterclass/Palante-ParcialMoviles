package com.example.recorrido

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import com.example.recorrido.sensores.RastreadorUbicacion
import com.example.recorrido.sensores.SensorLuz
import com.example.recorrido.sensores.SensorPasos
import com.google.android.gms.maps.model.LatLng
import java.io.File

class RecorridoViewModel(app: Application) : AndroidViewModel(app) {

    var recorridoActivo by mutableStateOf(false)
        private set
    var pasos by mutableIntStateOf(0)
        private set
    var lux by mutableFloatStateOf(-1f)
        private set
    var modoOscuro by mutableStateOf(false)
        private set
    var ubicacionActual by mutableStateOf<LatLng?>(null)
        private set

    val ruta = mutableStateListOf<LatLng>()
    val puntos = mutableStateListOf<PuntoInteres>()

    // distancia estimada multiplicando los pasos por la longitud promedio de un paso
    val distanciaMetros: Float get() = pasos * LONGITUD_PASO_M

    private val sensorPasos = SensorPasos(app) { pasos = it }
    private val sensorLuz = SensorLuz(app) { valor -> actualizarTema(valor) }
    private val rastreador = RastreadorUbicacion(app) { pos ->
        ubicacionActual = pos
        if (recorridoActivo) ruta.add(pos)
    }

    val tieneSensorLuz: Boolean get() = sensorLuz.disponible

    init {
        // el sensor de luz funciona en toda la app, no solo durante el recorrido
        sensorLuz.iniciar()
    }

    private fun actualizarTema(valor: Float) {
        lux = valor
        // se usan dos umbrales para que el tema no parpadee cerca del límite
        if (valor < LUX_OSCURO) modoOscuro = true
        else if (valor > LUX_CLARO) modoOscuro = false
    }

    private fun tienePermiso(permiso: String) =
        ContextCompat.checkSelfPermission(getApplication(), permiso) == PackageManager.PERMISSION_GRANTED

    fun tienePermisoUbicacion() =
        tienePermiso(Manifest.permission.ACCESS_FINE_LOCATION) ||
            tienePermiso(Manifest.permission.ACCESS_COARSE_LOCATION)

    fun tienePermisoCamara() = tienePermiso(Manifest.permission.CAMERA)

    fun iniciarRecorrido() {
        if (recorridoActivo) return
        limpiarCache()
        pasos = 0
        ruta.clear()
        puntos.clear()

        val permisoActividad = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
            tienePermiso(Manifest.permission.ACTIVITY_RECOGNITION)
        sensorPasos.iniciar(permisoActividad)
        if (tienePermisoUbicacion()) rastreador.iniciar()
        recorridoActivo = true
    }

    fun finalizarRecorrido() {
        sensorPasos.detener()
        rastreador.detener()
        recorridoActivo = false
    }

    /** Crea un archivo vacío en la caché donde la cámara guardará la foto. */
    fun crearUriFoto(): Uri {
        val contexto = getApplication<Application>()
        val carpeta = File(contexto.cacheDir, "fotos").apply { mkdirs() }
        val archivo = File(carpeta, "IMG_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(contexto, "${contexto.packageName}.fileprovider", archivo)
    }

    /** Se llama cuando la cámara devolvió la foto correctamente. */
    fun registrarFoto(uri: Uri) {
        val pos = ubicacionActual
        val ahora = System.currentTimeMillis()
        puntos.add(
            PuntoInteres(
                id = ahora,
                foto = uri,
                latitud = pos?.latitude,
                longitud = pos?.longitude,
                fecha = ahora
            )
        )
    }

    private fun limpiarCache() {
        File(getApplication<Application>().cacheDir, "fotos").listFiles()?.forEach { it.delete() }
    }

    override fun onCleared() {
        sensorLuz.detener()
        finalizarRecorrido()
        super.onCleared()
    }

    companion object {
        const val LONGITUD_PASO_M = 0.75f
        private const val LUX_OSCURO = 15f
        private const val LUX_CLARO = 40f
    }
}
