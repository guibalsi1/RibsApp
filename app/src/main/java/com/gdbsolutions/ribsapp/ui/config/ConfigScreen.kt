package com.gdbsolutions.ribsapp.ui.config

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen() {
    Text(text = "Tela Configurações", modifier = Modifier.fillMaxSize().wrapContentSize())
}