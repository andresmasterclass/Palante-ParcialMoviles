package com.example.recorrido

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recorrido.navigation.NavegacionApp
import com.example.recorrido.ui.theme.RecorridoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: RecorridoViewModel = viewModel()
            // el tema cambia solo según lo que marque el sensor de luz
            RecorridoTheme(darkTheme = vm.modoOscuro) {
                NavegacionApp(vm)
            }
        }
    }
}
