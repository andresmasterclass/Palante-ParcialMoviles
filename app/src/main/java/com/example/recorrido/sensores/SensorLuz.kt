package com.example.recorrido.sensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/** Lee el sensor de luz ambiental y devuelve el valor en lux. */
class SensorLuz(context: Context, private val onLux: (Float) -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    val disponible: Boolean get() = sensorLuz != null

    fun iniciar() {
        sensorLuz?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    fun detener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        onLux(event.values[0])
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
