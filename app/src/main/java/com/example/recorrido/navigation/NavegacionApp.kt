package com.example.recorrido.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.recorrido.RecorridoViewModel
import com.example.recorrido.ui.pantallas.PantallaInicio
import com.example.recorrido.ui.pantallas.PantallaPuntos
import com.example.recorrido.ui.pantallas.PantallaRecorrido
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destino : NavKey {

    @Serializable
    data object Inicio : Destino

    @Serializable
    data object Recorrido : Destino

    @Serializable
    data object Puntos : Destino
}

@Composable
fun NavegacionApp(vm: RecorridoViewModel, modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Destino.Inicio)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider = entryProvider {
            entry<Destino.Inicio> {
                PantallaInicio(vm, onIrARecorrido = { backStack.add(Destino.Recorrido) })
            }
            entry<Destino.Recorrido> {
                PantallaRecorrido(
                    vm,
                    onVerPuntos = { backStack.add(Destino.Puntos) },
                    onAtras = { backStack.removeLastOrNull() }
                )
            }
            entry<Destino.Puntos> {
                PantallaPuntos(vm, onAtras = { backStack.removeLastOrNull() })
            }
        }
    )
}
