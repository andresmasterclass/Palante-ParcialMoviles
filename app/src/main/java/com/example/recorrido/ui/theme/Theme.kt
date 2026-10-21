package com.example.recorrido.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresClaros = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF0B3D0F),
    secondary = Color(0xFF00796B),
    secondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFEF6C00),
    background = Color(0xFFF6F8F5),
    surface = Color.White,
    surfaceVariant = Color(0xFFE6ECE4)
)

private val ColoresOscuros = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF0B3D0F),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF4DB6AC),
    secondaryContainer = Color(0xFF004D40),
    tertiary = Color(0xFFFFB74D),
    background = Color(0xFF111411),
    surface = Color(0xFF1A1E1A),
    surfaceVariant = Color(0xFF2A302A)
)

@Composable
fun RecorridoTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
        content = content
    )
}
