package com.example.recorrido.ui.pantallas

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recorrido.R
import com.example.recorrido.RecorridoViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRecorrido(
    vm: RecorridoViewModel,
    onVerPuntos: () -> Unit,
    onAtras: () -> Unit
) {
    val context = LocalContext.current
    var uriPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }
    var confirmarFin by remember { mutableStateOf(false) }

    val launcherCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        val uri = uriPendiente
        if (exito && uri != null) {
            vm.registrarFoto(uri)
            Toast.makeText(context, "Punto de interés guardado", Toast.LENGTH_SHORT).show()
        }
        uriPendiente = null
    }

    fun abrirCamara() {
        val uri = vm.crearUriFoto()
        uriPendiente = uri
        launcherCamara.launch(uri)
    }

    // por si el usuario negó la cámara al inicio, se vuelve a pedir aquí
    val launcherPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara()
        else Toast.makeText(context, "Se necesita el permiso de cámara", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (vm.recorridoActivo) "Recorrido en curso" else "Recorrido finalizado") },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onVerPuntos) {
                        BadgedBox(badge = {
                            if (vm.puntos.isNotEmpty()) Badge { Text("${vm.puntos.size}") }
                        }) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = "Puntos de interés")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (vm.recorridoActivo) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (vm.tienePermisoCamara()) abrirCamara()
                        else launcherPermisoCamara.launch(Manifest.permission.CAMERA)
                    },
                    icon = { Icon(Icons.Filled.CameraAlt, contentDescription = null) },
                    text = { Text("Tomar foto") }
                )
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Mapa(vm)

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TarjetaDato(Icons.Filled.DirectionsWalk, "Pasos", "${vm.pasos}", Modifier.weight(1f))
                    TarjetaDato(
                        Icons.Filled.Straighten, "Distancia",
                        formatearDistancia(vm.distanciaMetros), Modifier.weight(1f)
                    )
                    TarjetaDato(Icons.Filled.Place, "Puntos", "${vm.puntos.size}", Modifier.weight(1f))
                }
            }

            Row(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                if (vm.recorridoActivo) {
                    Button(
                        onClick = { confirmarFin = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Filled.Stop, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Finalizar")
                    }
                } else {
                    OutlinedButton(onClick = onVerPuntos) {
                        Icon(Icons.Filled.Dashboard, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Ver resumen")
                    }
                }
            }
        }
    }

    if (confirmarFin) {
        AlertDialog(
            onDismissRequest = { confirmarFin = false },
            title = { Text("¿Finalizar recorrido?") },
            text = { Text("Se dejarán de contar los pasos y de registrar la ruta.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarFin = false
                    vm.finalizarRecorrido()
                    onVerPuntos()
                }) { Text("Finalizar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarFin = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun Mapa(vm: RecorridoViewModel) {
    val context = LocalContext.current
    val camara = rememberCameraPositionState()
    var centrado by remember { mutableStateOf(false) }

    // la cámara del mapa sigue al usuario cuando cambia su ubicación
    LaunchedEffect(vm.ubicacionActual) {
        val pos = vm.ubicacionActual ?: return@LaunchedEffect
        if (!centrado) {
            camara.move(CameraUpdateFactory.newLatLngZoom(pos, 17f))
            centrado = true
        } else {
            camara.animate(CameraUpdateFactory.newLatLng(pos), 800)
        }
    }

    val estiloOscuro = remember { MapStyleOptions.loadRawResourceStyle(context, R.raw.mapa_oscuro) }
    val colorRuta = MaterialTheme.colorScheme.primary
    val formato = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camara,
        properties = MapProperties(
            isMyLocationEnabled = vm.tienePermisoUbicacion(),
            mapStyleOptions = if (vm.modoOscuro) estiloOscuro else null
        ),
        uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = true),
        contentPadding = PaddingValues(top = 90.dp, bottom = 70.dp)
    ) {
        if (vm.ruta.size > 1) {
            Polyline(
                points = vm.ruta.toList(),
                color = colorRuta,
                width = 12f
            )
        }
        vm.puntos.filter { it.tieneUbicacion }.forEachIndexed { i, punto ->
            key(punto.id) {
                Marker(
                    state = rememberMarkerState(position = LatLng(punto.latitud!!, punto.longitud!!)),
                    title = "Punto de interés ${i + 1}",
                    snippet = formato.format(Date(punto.fecha)),
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                )
            }
        }
    }
}

@Composable
private fun TarjetaDato(icono: ImageVector, titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(titulo, style = MaterialTheme.typography.labelSmall)
        }
    }
}

fun formatearDistancia(metros: Float): String =
    if (metros < 1000) "${metros.toInt()} m" else String.format(Locale.getDefault(), "%.2f km", metros / 1000)
