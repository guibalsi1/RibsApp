package com.gdbsolutions.ribsapp

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gdbsolutions.ribsapp.ui.config.DeleteAdicionalScreen
import com.gdbsolutions.ribsapp.ui.config.DeleteCarneScreen
import com.gdbsolutions.ribsapp.ui.config.DeleteEntradaScreen
import com.gdbsolutions.ribsapp.ui.config.DeletePratoScreen
import com.gdbsolutions.ribsapp.ui.config.FalarComDevScreen
import com.gdbsolutions.ribsapp.ui.config.PagarCafeScreen
import com.gdbsolutions.ribsapp.ui.config.SettingsScreen
import com.gdbsolutions.ribsapp.ui.criar.CriarEventoScreen
import com.gdbsolutions.ribsapp.ui.criar.CriarEventoViewModel
import com.gdbsolutions.ribsapp.ui.historico.HistoricoScreen
import com.gdbsolutions.ribsapp.ui.theme.RibsAppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RibsAppTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(appName: String, logo: Painter) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = appName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.background
            )
        },
        actions = {
            Icon(
                painter = logo,
                contentDescription = "Logo",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.background
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.CriarEvento,
        BottomNavItem.Historico,
        BottomNavItem.Settings
    )
    val logo = painterResource(id = R.drawable.logo_ribs)
    val contexto = LocalContext.current.applicationContext as Application
    val viewModel: CriarEventoViewModel = viewModel(
        factory = CriarEventoViewModel.EventoViewModelFactory(contexto)
    )

    Scaffold(
        topBar = {
            MyTopBar("Ribs BBQ", logo)
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.background) {
                val currentBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry.value?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.CriarEvento.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.CriarEvento.route) { CriarEventoScreen(viewModel = viewModel) }
            composable(BottomNavItem.Historico.route) { HistoricoScreen(viewModel = viewModel) }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    onExcluirCarnes = { navController.navigate(ConfigPaths.DeleteCarnes.route) },
                    onExcluirEntradas = { navController.navigate(ConfigPaths.DeleteEntradas.route) },
                    onExcluirPratos = { navController.navigate(ConfigPaths.DeletePratos.route) },
                    onExcluirAdicionais = { navController.navigate(ConfigPaths.DeleteAdicionais.route) },
                    onConversarComDesenvolvedor = { navController.navigate(ConfigPaths.ConversarComDesenvolvedor.route) },
                    onPagarCafe = { navController.navigate(ConfigPaths.PagarCafe.route) }
                )
            }
            composable(ConfigPaths.DeleteCarnes.route) { DeleteCarneScreen() }
            composable(ConfigPaths.DeleteEntradas.route) { DeleteEntradaScreen() }
            composable(ConfigPaths.DeletePratos.route) { DeletePratoScreen() }
            composable(ConfigPaths.DeleteAdicionais.route) { DeleteAdicionalScreen() }
            composable(ConfigPaths.ConversarComDesenvolvedor.route) { FalarComDevScreen() }
            composable(ConfigPaths.PagarCafe.route) { PagarCafeScreen() }
        }

    }
}

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object CriarEvento : BottomNavItem("criar", "Criar Evento", Icons.Default.Edit)
    object Historico : BottomNavItem("historico", "Histórico", Icons.Default.DateRange)
    object Settings : BottomNavItem("config", "Config", Icons.Default.Settings)
}

sealed class ConfigPaths (val route: String) {
    object DeleteCarnes: ConfigPaths("deleteCarnes")
    object DeleteEntradas: ConfigPaths("deleteEntradas")
    object DeletePratos: ConfigPaths("deletePratos")
    object DeleteAdicionais: ConfigPaths("deleteAdicionais")
    object ConversarComDesenvolvedor: ConfigPaths("conversarComDesenvolvedor")
    object PagarCafe: ConfigPaths("pagarCafe")
}