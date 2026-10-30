package com.example.protocolosinduztekapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.protocolosinduztekapp.navegations.AppNavigation
import com.example.protocolosinduztekapp.view.theme.ProtocolosInduztekAppTheme
import com.example.protocolosinduztekapp.viewmodels.PruebaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProtocolosInduztekAppTheme {
                val viewModel: PruebaViewModel = viewModel()
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}