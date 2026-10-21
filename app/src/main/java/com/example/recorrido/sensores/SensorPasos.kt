package com.example.recorrido.sensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Cuenta los pasos usando el sensor TYPE_STEP_COUNTER.
 * Si el dispositivo no lo tiene (o no se dio el permiso de actividad física),
 * se usa el acelerómetro detectando los picos de aceleración de cada paso.
 */
class SensorPasos(context: Context, private val onPasos: (Int) -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val contadorPasos = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var valorInicial = -1f
    private var pasos = 0

    // variables para la detección con acelerómetro
    private var enPico = false
    private var ultimoPaso = 0L

    fun iniciar(permisoActividad: Boolean) {
        pasos = 0
        valorInicial = -1f
        if (contadorPasos != null && permisoActividad) {
            sensorManager.registerListener(this, contadorPasos, SensorManager.SENSOR_DELAY_UI)
        } else if (acelerometro != null) {
            sensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun detener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                // el contador entrega los pasos desde que se encendió el teléfono
                if (valorInicial < 0) valorInicial = event.values[0]
                pasos = (event.values[0] - valorInicial).toInt()
                onPasos(pasos)
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitud = sqrt(x * x + y * y + z * z)
                val ahora = System.currentTimeMillis()

                if (!enPico && magnitud > UMBRAL_ALTO && ahora - ultimoPaso > TIEMPO_MIN_MS) {
                    enPico = true
                    ultimoPaso = ahora
                    pasos++
                    onPasos(pasos)
                } else if (magnitud < UMBRAL_BAJO) {
                    enPico = false
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        private const val UMBRAL_ALTO = 11.5f
        private const val UMBRAL_BAJO = 9.5f
        private const val TIEMPO_MIN_MS = 300L
    }
}
