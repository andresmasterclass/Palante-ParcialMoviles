package com.example.recorrido.sensores

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

/** Obtiene la ubicación del GPS cada pocos segundos con FusedLocationProvider. */
class RastreadorUbicacion(context: Context, private val onUbicacion: (LatLng) -> Unit) {

    private val cliente = LocationServices.getFusedLocationProviderClient(context)

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { onUbicacion(LatLng(it.latitude, it.longitude)) }
        }
    }

    // Solo se llama después de verificar el permiso de ubicación
    @SuppressLint("MissingPermission")
    fun iniciar() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateDistanceMeters(2f)
            .build()
        cliente.lastLocation.addOnSuccessListener { loc ->
            loc?.let { onUbicacion(LatLng(it.latitude, it.longitude)) }
        }
        cliente.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    fun detener() {
        cliente.removeLocationUpdates(callback)
    }
}
