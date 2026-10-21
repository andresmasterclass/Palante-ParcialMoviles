package com.example.recorrido

import android.net.Uri

data class PuntoInteres(
    val id: Long,
    val foto: Uri,
    val latitud: Double?,
    val longitud: Double?,
    val fecha: Long
) {
    val tieneUbicacion: Boolean get() = latitud != null && longitud != null
}
