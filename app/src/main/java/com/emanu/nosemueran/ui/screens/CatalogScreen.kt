package com.emanu.nosemueran.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emanu.nosemueran.data.Prefill
import com.emanu.nosemueran.data.Species
import com.emanu.nosemueran.data.SpeciesCatalog
import com.emanu.nosemueran.data.SpeciesCategory
import com.emanu.nosemueran.data.SowingCalendar
import com.emanu.nosemueran.ui.MainViewModel
import com.emanu.nosemueran.ui.components.InfoChip
import com.emanu.nosemueran.ui.components.SectionLabel
import com.emanu.nosemueran.ui.components.SowingInfo
import com.emanu.nosemueran.ui.theme.Clay
import com.emanu.nosemueran.ui.theme.LeafDark
import com.emanu.nosemueran.ui.theme.LeafGreen
import com.emanu.nosemueran.ui.theme.Muted
import com.emanu.nosemueran.ui.theme.Paper
import com.emanu.nosemueran.ui.theme.Sage
import com.emanu.nosemueran.ui.theme.SoftMint
import com.emanu.nosemueran.ui.theme.Sunny
import java.time.LocalDate

@Composable
fun CatalogScreen(nav: NavController) {
    var tab by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<SpeciesCategory?>(null) }

    val filtered = remember(query, category) {
        SpeciesCatalog.search(query).filter { category == null || it.category == category }
    }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp)) {
            Text("Catálogo", style = MaterialTheme.typography.displaySmall, color = LeafDark)
            Spacer(Modifier.height(2.dp))
            Text(
                "Cuidados de ${SpeciesCatalog.all.size} especies",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TabChip("🌿 Especies", tab == 0) { tab = 0 }
                TabChip("🗓 Temporada", tab == 1) { tab = 1 }
            }
            if (tab == 0) {
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Buscar una planta...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Sage) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CategoryChip("Todas", category == null) { category = null }
                    SpeciesCategory.values().forEach { cat ->
                        CategoryChip("${cat.emoji} ${cat.label}", category == cat) { category = cat }
                    }
                }
            }
        }

        if (tab == 0) {
            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No encontré esa planta.\nProbá con otro nombre.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Muted
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpeciesCategory.values().forEach { cat ->
                        val list = filtered.filter { it.category == cat }
                        if (list.isNotEmpty()) {
                            item(key = "header-${cat.name}") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 6.dp)
                                ) {
                                    Text(cat.emoji, fontSize = 14.sp)
                                    Spacer(Modifier.width(6.dp))
                                    SectionLabel("${cat.label} · ${list.size}")
                                }
                            }
                            items(list, key = { it.name }) { species ->
                                SpeciesRow(species) {
                                    nav.navigate("especie/${Uri.encode(species.name)}")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            SeasonTab(nav)
        }
    }
}

@Composable
private fun TabChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = if (selected) LeafGreen else SoftMint,
        contentColor = if (selected) Color.White else LeafDark,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = if (selected) LeafDark else Paper,
        contentColor = if (selected) Color.White else Muted,
        border = if (selected) null else BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
        ),
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun SeasonTab(nav: NavController) {
    var month by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    val guide = remember(month) { SowingCalendar.forMonth(month) }
    val sowable = remember(month) { SpeciesCatalog.sowableIn(month) }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SowingCalendar.monthNames.forEachIndexed { index, name ->
                    val value = index + 1
                    Surface(
                        shape = CircleShape,
                        color = if (value == month) LeafGreen else Paper,
                        contentColor = if (value == month) Color.White else Muted,
                        border = if (value == month) null else BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { month = value }
                    ) {
                        Text(
                            name.take(3),
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SoftMint,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        "Qué sembrar en ${guide.name}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = LeafDark
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        guide.tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LeafDark
                    )
                }
            }
        }

        item {
            Column {
                SectionLabel("${monthName(month)} · ${sowable.size} especies")
                Spacer(Modifier.height(4.dp))
                Text(
                    SowingCalendar.ZONE_NOTE,
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
        }

        if (sowable.isEmpty()) {
            item {
                Text(
                    "Este mes la huerta de la zona central está en pausa. Podés preparar la tierra y hacer almácigos bajo techo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }
        } else {
            items(sowable, key = { "sow-${it.name}" }) { species ->
                SowingRow(species) {
                    nav.navigate("especie/${Uri.encode(species.name)}")
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text(
                "Las fechas son orientativas: dependen del microclima de tu patio, de si hay heladas y de la variedad que siembres.",
                style = MaterialTheme.typography.bodySmall,
                color = Muted
            )
        }
    }
}

private fun monthName(month: Int): String = SowingCalendar.forMonth(month).name

@Composable
private fun SowingRow(species: Species, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SoftMint),
                contentAlignment = Alignment.Center
            ) {
                Text(species.emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    species.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = LeafDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    buildString {
                        append(species.sowingMethod?.label ?: "Siembra")
                        species.harvestDays?.let { append(" · ~$it días") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Sage)
        }
    }
}

@Composable
private fun SpeciesRow(species: Species, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Paper),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SoftMint),
                contentAlignment = Alignment.Center
            ) {
                Text(species.emoji, fontSize = 24.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    species.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = LeafDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(1.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        species.scientific,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = Muted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(" · ", style = MaterialTheme.typography.bodySmall, color = Muted)
                    Text(
                        species.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        color = difficultyColor(species.difficulty)
                    )
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Sage)
        }
    }
}

@Composable
fun SpeciesDetailScreen(vm: MainViewModel, nav: NavController, speciesName: String) {
    val species = remember(speciesName) { SpeciesCatalog.find(speciesName) }

    if (species == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No encontré esa especie.", color = Muted)
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(Brush.linearGradient(listOf(Color(0xFFE7EFE1), Color(0xFFCFE0CB))))
        ) {
            Text(
                species.emoji,
                fontSize = 92.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = { nav.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Paper.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = LeafDark)
            }
        }

        Column(Modifier.padding(20.dp)) {
            Text(species.name, style = MaterialTheme.typography.displaySmall, color = LeafDark)
            Text(
                species.scientific,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = Muted
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip("${species.category.emoji} ${species.category.label}")
                InfoChip(species.difficulty, highlight = species.difficulty == "Alta")
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip("Riego cada ${species.wateringDays} días", Icons.Filled.WaterDrop)
            }
            Spacer(Modifier.height(16.dp))
            Text(species.description, style = MaterialTheme.typography.bodyLarge, color = LeafDark)

            if (species.sowsInCentralChile) {
                Spacer(Modifier.height(20.dp))
                SectionLabel("Siembra en la zona central")
                Spacer(Modifier.height(8.dp))
                SowingInfo(species)
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("Cuidados")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CareCard(Icons.Filled.WbSunny, "Luz", species.light, Modifier.weight(1f))
                CareCard(Icons.Filled.WaterDrop, "Agua", species.water, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CareCard(Icons.Outlined.Opacity, "Humedad", species.humidity, Modifier.weight(1f))
                CareCard(Icons.Outlined.Thermostat, "Temperatura", species.temp, Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("Consejos")
            Spacer(Modifier.height(10.dp))
            species.tips.forEach { tip ->
                Row(Modifier.padding(bottom = 8.dp)) {
                    Text("•", color = Sage, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.width(8.dp))
                    Text(tip, style = MaterialTheme.typography.bodyMedium, color = LeafDark)
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    vm.applyPrefill(Prefill(species.name, species.wateringDays))
                    nav.navigate("agregar")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LeafGreen)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Agregar a mi jardín")
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CareCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Paper,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
    ) {
        Column(Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = Sage, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(8.dp))
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Sage)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, color = LeafDark)
        }
    }
}

private fun difficultyColor(difficulty: String): Color = when (difficulty) {
    "Muy fácil", "Fácil" -> Sage
    "Media" -> Sunny
    else -> Clay
}
