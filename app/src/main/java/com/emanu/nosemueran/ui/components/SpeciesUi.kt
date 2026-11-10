package com.emanu.nosemueran.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.emanu.nosemueran.data.Species
import com.emanu.nosemueran.data.SpeciesCatalog
import com.emanu.nosemueran.data.SpeciesCategory
import com.emanu.nosemueran.data.SowingCalendar
import com.emanu.nosemueran.ui.theme.LeafDark
import com.emanu.nosemueran.ui.theme.LeafGreen
import com.emanu.nosemueran.ui.theme.Muted
import com.emanu.nosemueran.ui.theme.Paper
import com.emanu.nosemueran.ui.theme.Sage
import com.emanu.nosemueran.ui.theme.SoftMint

/** Diálogo con buscador y especies agrupadas por categoría. */
@Composable
fun SpeciesPickerDialog(
    title: String = "Elegir especie",
    initialQuery: String = "",
    onDismiss: () -> Unit,
    onPick: (Species) -> Unit
) {
    var query by remember { mutableStateOf(initialQuery) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = Paper,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = LeafDark,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", tint = Muted)
                    }
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Buscar tomate, lechuga, papa...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Sage) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))

                val results = remember(query) { SpeciesCatalog.search(query) }
                val groups = SpeciesCategory.values().mapNotNull { category ->
                    val list = results.filter { it.category == category }
                    if (list.isEmpty()) null else category to list
                }

                if (groups.isEmpty()) {
                    Text(
                        "No encontré esa planta. Probá con otro nombre.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Muted,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                } else {
                    LazyColumn(Modifier.heightIn(max = 520.dp)) {
                        groups.forEach { (category, list) ->
                            item(key = "cat-${category.name}") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                ) {
                                    Text(category.emoji, fontSize = 15.sp)
                                    Spacer(Modifier.width(6.dp))
                                    SectionLabel(category.label)
                                }
                            }
                            items(list, key = { it.name }) { species ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { onPick(species) }
                                        .padding(horizontal = 8.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(SoftMint),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(species.emoji, fontSize = 19.sp)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            species.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = LeafDark
                                        )
                                        Text(
                                            species.scientific,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontStyle = FontStyle.Italic,
                                            color = Muted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Tarjeta con los cuidados de una especie, para el formulario y el detalle. */
@Composable
fun SpeciesCareCard(
    species: Species,
    onOpenDetail: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Paper),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(species.emoji, fontSize = 26.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Cuidados de ${species.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = LeafDark
                    )
                    Text(
                        species.category.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoChip(species.light, Icons.Filled.WbSunny)
                InfoChip("Cada ${species.wateringDays} d", Icons.Filled.WaterDrop)
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoChip("Humedad ${species.humidity.lowercase()}", Icons.Outlined.Thermostat)
                InfoChip(species.difficulty)
            }
            Spacer(Modifier.height(10.dp))
            species.tips.take(3).forEach { tip ->
                Row(Modifier.padding(bottom = 6.dp)) {
                    Text("•", color = Sage, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = LeafDark
                    )
                }
            }
            if (species.sowsInCentralChile) {
                Spacer(Modifier.height(4.dp))
                SowingInfo(species)
            }
            if (onOpenDetail != null) {
                TextButton(onClick = onOpenDetail) {
                    Text("Ver ficha completa", color = LeafGreen)
                }
            }
        }
    }
}

/** Detalle de siembra: meses, método y días a cosecha. */
@Composable
fun SowingInfo(species: Species) {
    val months = species.sowMonths.sorted()
    val label = when {
        months.size == 12 -> "Todo el año"
        months.isEmpty() -> ""
        else -> months.joinToString(", ") { SowingCalendar.shortName(it) }
    }
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SoftMint,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                "Siembra en zona central",
                style = MaterialTheme.typography.labelSmall,
                color = Sage
            )
            Spacer(Modifier.height(2.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = LeafDark)
            species.sowingMethod?.let {
                Text(
                    "${it.label}${species.harvestDays?.let { days -> " · cosecha en ~$days días" } ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
            if (species.sowingNote.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    species.sowingNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = LeafDark
                )
            }
        }
    }
}
