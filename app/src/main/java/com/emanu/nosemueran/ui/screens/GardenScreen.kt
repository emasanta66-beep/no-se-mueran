package com.emanu.nosemueran.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.emanu.nosemueran.data.Plant
import com.emanu.nosemueran.data.SpeciesCatalog
import com.emanu.nosemueran.ui.MainViewModel
import com.emanu.nosemueran.ui.components.PlantPhoto
import com.emanu.nosemueran.ui.components.StatPill
import com.emanu.nosemueran.ui.nextWateringLabel
import com.emanu.nosemueran.ui.theme.Clay
import com.emanu.nosemueran.ui.theme.LeafDark
import com.emanu.nosemueran.ui.theme.LeafGreen
import com.emanu.nosemueran.ui.theme.Muted
import com.emanu.nosemueran.ui.theme.Paper
import com.emanu.nosemueran.ui.theme.Sage
import com.emanu.nosemueran.ui.todayLabel

@Composable
fun GardenScreen(vm: MainViewModel, nav: NavController) {
    val plants by vm.plants.collectAsStateWithLifecycle()

    // Al volver a la app recalculamos "toca regar" aunque la lista no haya cambiado.
    var dayTick by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) dayTick++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val due = remember(plants, dayTick) { plants.count { it.isDue() } }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 12.dp, top = 20.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "No se mueran",
                        style = MaterialTheme.typography.displaySmall,
                        color = LeafDark
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(todayLabel(), style = MaterialTheme.typography.bodyMedium, color = Muted)
                }
                IconButton(onClick = { nav.navigate("ajustes") }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Ajustes", tint = LeafDark)
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatPill(
                    text = "${plants.size} planta${if (plants.size == 1) "" else "s"}",
                    icon = Icons.Outlined.LocalFlorist
                )
                if (due > 0) {
                    StatPill(
                        text = "$due para regar",
                        icon = Icons.Filled.WaterDrop,
                        highlight = true
                    )
                }
            }

            if (plants.isEmpty()) {
                EmptyGarden(
                    onAdd = { nav.navigate("agregar") },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 16.dp),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 112.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(plants, key = { it.id }) { plant ->
                        PlantCard(plant, dayTick) { nav.navigate("planta/${plant.id}") }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { nav.navigate("agregar") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = LeafGreen,
            contentColor = Color.White,
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text("Agregar") }
        )
    }
}

@Composable
private fun EmptyGarden(onAdd: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🌱", fontSize = 64.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            "Tu jardín está vacío",
            style = MaterialTheme.typography.headlineSmall,
            color = LeafDark
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Agregá tu primera planta y te voy a avisar cuándo regarla.",
            style = MaterialTheme.typography.bodyMedium,
            color = Muted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(containerColor = LeafGreen),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.height(50.dp)
        ) {
            Text("Agregar mi primera planta")
        }
    }
}

@Composable
private fun PlantCard(plant: Plant, dayTick: Int, onClick: () -> Unit) {
    val due = remember(plant, dayTick) { plant.isDue() }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                PlantPhoto(
                    path = plant.photoPath,
                    emoji = SpeciesCatalog.emojiFor(plant.species),
                    modifier = Modifier.fillMaxSize(),
                    fontSize = 52.sp
                )
                if (due) {
                    Surface(
                        color = Clay,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            "Regar",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            Column(Modifier.padding(14.dp)) {
                Text(
                    plant.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = LeafDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    plant.species.ifBlank { "Sin especie" },
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = if (due) Clay else Sage,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        nextWateringLabel(plant),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (due) Clay else Muted
                    )
                }
            }
        }
    }
}
