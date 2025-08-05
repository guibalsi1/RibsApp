package com.gdbsolutions.ribsapp.ui.config

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onExcluirCarnes: () -> Unit = {},
    onExcluirEntradas: () -> Unit = {},
    onExcluirPratos: () -> Unit = {},
    onExcluirAdicionais: () -> Unit = {},
    onConversarComDesenvolvedor: () -> Unit = {},
    onPagarCafe: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Configurações",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SessaoTitulo("Editar")
        ItemConfiguracao("Excluir Carnes", Icons.Default.Delete, onExcluirCarnes)
        ItemConfiguracao("Excluir Entradas", Icons.Default.Delete, onExcluirEntradas)
        ItemConfiguracao("Excluir Pratos", Icons.Default.Delete, onExcluirPratos)
        ItemConfiguracao("Excluir Adicionais", Icons.Default.Delete, onExcluirAdicionais)

        Spacer(modifier = Modifier.height(32.dp))

        SessaoTitulo("Sobre o Aplicativo")
        ItemConfiguracao("Conversar com o desenvolvedor", Icons.Default.Email, onConversarComDesenvolvedor)
        ItemConfiguracao("Pagar um café", Icons.Default.ShoppingCart, onPagarCafe)
    }
}

@Composable
fun SessaoTitulo(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ItemConfiguracao(
    texto: String,
    icone: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(texto, fontSize = 16.sp) },
        leadingContent = {
            Icon(
                imageVector = icone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}