package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.VeloFleetApp
import com.example.ui.theme.VeloFleetTheme
import com.example.ui.viewmodel.FleetViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FleetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VeloFleetTheme {
                VeloFleetApp(viewModel = viewModel)
            }
        }
    }
}
