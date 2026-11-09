package com.emanu.nosemueran.ui

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.emanu.nosemueran.ui.screens.AddPlantScreen
import com.emanu.nosemueran.ui.screens.CatalogScreen
import com.emanu.nosemueran.ui.screens.DiagnoseScreen
import com.emanu.nosemueran.ui.screens.GardenScreen
import com.emanu.nosemueran.ui.screens.PlantDetailScreen
import com.emanu.nosemueran.ui.screens.SettingsScreen
import com.emanu.nosemueran.ui.screens.SpeciesDetailScreen
import com.emanu.nosemueran.ui.theme.LeafGreen
import com.emanu.nosemueran.ui.theme.Muted
import com.emanu.nosemueran.ui.theme.Paper
import com.emanu.nosemueran.ui.theme.SoftMint

private data class Tab(
    val route: String,
    val label: String,
    val filled: ImageVector,
    val outlined: ImageVector
)

private val tabs = listOf(
    Tab("jardin", "Jardín", Icons.Filled.LocalFlorist, Icons.Outlined.LocalFlorist),
    Tab("catalogo", "Catálogo", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    Tab("diagnostico", "Diagnóstico", Icons.Filled.PhotoCamera, Icons.Outlined.PhotoCamera),
    Tab("ajustes", "Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings)
)

private val topLevelRoutes = tabs.map { it.route }.toSet()

@Composable
fun AppNav(openUpdates: Boolean = false, onUpdatesHandled: () -> Unit = {}) {
    val nav = rememberNavController()
    val vm: MainViewModel = viewModel()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Si se abrió desde el aviso de actualización, llevamos a Ajustes.
    LaunchedEffect(openUpdates) {
        if (openUpdates) {
            nav.navigate("ajustes") { launchSingleTop = true }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute == null || currentRoute in topLevelRoutes) {
                AppBottomBar(currentRoute, nav)
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "jardin",
            modifier = Modifier.padding(padding)
        ) {
            composable("jardin") { GardenScreen(vm, nav) }
            composable("catalogo") { CatalogScreen(nav) }
            composable("diagnostico") { DiagnoseScreen() }
            composable("ajustes") {
                SettingsScreen(vm, autoCheck = openUpdates, onAutoCheck = onUpdatesHandled)
            }

            composable("planta/{id}") { entry ->
                PlantDetailScreen(vm, nav, entry.arguments?.getString("id").orEmpty())
            }
            composable("agregar") { AddPlantScreen(vm, nav, null) }
            composable("editar/{id}") { entry ->
                AddPlantScreen(vm, nav, entry.arguments?.getString("id"))
            }
            composable("especie/{nombre}") { entry ->
                SpeciesDetailScreen(
                    vm,
                    nav,
                    Uri.decode(entry.arguments?.getString("nombre").orEmpty())
                )
            }
        }
    }
}

@Composable
private fun AppBottomBar(currentRoute: String?, nav: NavController) {
    NavigationBar(
        containerColor = Paper,
        tonalElevation = 0.dp
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { navigateTab(nav, tab.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.filled else tab.outlined,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = LeafGreen,
                    selectedTextColor = LeafGreen,
                    indicatorColor = SoftMint,
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted
                )
            )
        }
    }
}

private fun navigateTab(nav: NavController, route: String) {
    nav.navigate(route) {
        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
