package com.emanu.nosemueran.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.emanu.nosemueran.data.Species
import com.emanu.nosemueran.data.SpeciesCatalog
import com.emanu.nosemueran.ui.MainViewModel
import com.emanu.nosemueran.ui.components.ScreenHeader
import com.emanu.nosemueran.ui.components.SectionLabel
import com.emanu.nosemueran.ui.components.SpeciesCareCard
import com.emanu.nosemueran.ui.components.SpeciesPickerDialog
import com.emanu.nosemueran.ui.components.rememberPhotoPicker
import com.emanu.nosemueran.ui.theme.Clay
import com.emanu.nosemueran.ui.theme.LeafDark
import com.emanu.nosemueran.ui.theme.LeafGreen
import com.emanu.nosemueran.ui.theme.Muted
import com.emanu.nosemueran.ui.theme.Paper
import com.emanu.nosemueran.ui.theme.Sage
import com.emanu.nosemueran.ui.theme.SoftMint
import com.emanu.nosemueran.util.ImageAnalysis
import com.emanu.nosemueran.util.PhotoAnalysis
import com.emanu.nosemueran.util.PhotoHints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun AddPlantScreen(vm: MainViewModel, nav: NavController, editId: String?) {
    val plants by vm.plants.collectAsStateWithLifecycle()
    val editing = remember(editId, plants) {
        editId?.let { id -> plants.firstOrNull { it.id == id } }
    }

    var name by remember(editId) { mutableStateOf(editing?.name.orEmpty()) }
    var species by remember(editId) { mutableStateOf(editing?.species.orEmpty()) }
    var location by remember(editId) { mutableStateOf(editing?.location.orEmpty()) }
    var notes by remember(editId) { mutableStateOf(editing?.notes.orEmpty()) }
    var days by remember(editId) { mutableIntStateOf(editing?.wateringDays ?: 7) }
    var photo by remember(editId) { mutableStateOf(editing?.photoPath) }
    var showPicker by remember { mutableStateOf(false) }
    var analysis by remember(editId) { mutableStateOf<PhotoAnalysis?>(null) }

    val matched = remember(species) { SpeciesCatalog.find(species) }

    fun applySpecies(sp: Species) {
        species = sp.name
        days = sp.wateringDays
    }

    val picker = rememberPhotoPicker { path ->
        // Si se reemplaza una foto temporal, borramos la anterior.
        vm.discardPhoto(photo)
        photo = path
    }

    // Analizamos la foto para sugerir especies (aproximado, por colores).
    LaunchedEffect(photo) {
        analysis = photo?.let { path ->
            withContext(Dispatchers.Default) { ImageAnalysis.analyzeFile(path) }
        }
    }

    val photoHints = remember(analysis, species) {
        val a = analysis
        if (a == null || species.isNotBlank()) emptyList() else PhotoHints.candidates(a)
    }

    // Si se sale de la pantalla sin guardar, la foto temporal no queda huérfana.
    // discardPhoto respeta las fotos que ya estén referenciadas por una planta o diario.
    val currentPhoto by rememberUpdatedState(photo)
    DisposableEffect(Unit) {
        onDispose { vm.discardPhoto(currentPhoto) }
    }

    LaunchedEffect(Unit) {
        val prefill = vm.prefill
        if (editId == null && prefill != null) {
            if (species.isBlank()) species = prefill.speciesName
            days = prefill.wateringDays
            vm.applyPrefill(null)
        }
    }

    val suggestions = remember(species) {
        val q = species.trim()
        if (q.isBlank()) emptyList()
        else SpeciesCatalog.all.filter {
            it.name.contains(q, ignoreCase = true) || it.scientific.contains(q, ignoreCase = true)
        }.take(4)
    }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(
            title = if (editId == null) "Nueva planta" else "Editar planta",
            onBack = { nav.popBackStack() }
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 28.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SoftMint),
                contentAlignment = Alignment.Center
            ) {
                if (photo != null) {
                    AsyncImage(
                        model = File(photo!!),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = {
                            vm.discardPhoto(photo)
                            photo = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Paper.copy(alpha = 0.92f), CircleShape)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Quitar foto", tint = Clay)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📷", fontSize = 42.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Una foto ayuda a verla crecer",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = picker.takePhoto,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cámara")
                }
                OutlinedButton(
                    onClick = picker.pickFromGallery,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Galería")
                }
            }

            if (photoHints.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SoftMint,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            "Según los colores de la foto puede ser...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                        Spacer(Modifier.height(8.dp))
                        photoHints.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { hintName ->
                                    val hint = SpeciesCatalog.find(hintName)
                                    if (hint != null) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Paper,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(CircleShape)
                                                .clickable { applySpecies(hint) }
                                        ) {
                                            Text(
                                                "${hint.emoji} ${hint.name}",
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = LeafDark,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Text(
                            "Es una ayuda aproximada, no reconoce la especie exacta. Podés elegir otra abajo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            AppField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre",
                placeholder = "Ej: Monstera del living"
            )

            Spacer(Modifier.height(14.dp))
            AppField(
                value = species,
                onValueChange = { species = it },
                label = "Especie (opcional)",
                placeholder = "Ej: Tomate, Lechuga, Papa..."
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showPicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.LocalFlorist, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Elegir del catálogo")
            }
            if (suggestions.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    suggestions.take(3).forEach { suggestion ->
                        Surface(
                            shape = CircleShape,
                            color = SoftMint,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { applySpecies(suggestion) }
                        ) {
                            Text(
                                "${suggestion.emoji} ${suggestion.name}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = LeafDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            if (matched != null) {
                Spacer(Modifier.height(12.dp))
                SpeciesCareCard(
                    species = matched,
                    onOpenDetail = { nav.navigate("especie/${Uri.encode(matched.name)}") }
                )
            }

            Spacer(Modifier.height(14.dp))
            AppField(
                value = location,
                onValueChange = { location = it },
                label = "Ubicación (opcional)",
                placeholder = "Living, balcón, cocina..."
            )

            Spacer(Modifier.height(22.dp))
            SectionLabel("Riego")
            Spacer(Modifier.height(10.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Paper),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleAction(Icons.Filled.Remove) { days = (days - 1).coerceAtLeast(1) }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "$days",
                            style = MaterialTheme.typography.headlineMedium,
                            color = LeafDark
                        )
                        Text(
                            if (days == 1) "día" else "días",
                            style = MaterialTheme.typography.labelSmall,
                            color = Muted
                        )
                    }
                    CircleAction(Icons.Filled.Add) { days = (days + 1).coerceAtMost(90) }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(2, 3, 5, 7, 14, 21, 30).forEach { option ->
                    DayChip(
                        label = "$option d",
                        selected = days == option,
                        onClick = { days = option }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            AppField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notas (opcional)",
                placeholder = "Dónde la compraste, cómo está, cuidados especiales...",
                minLines = 3
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (editId == null) {
                        vm.createPlant(name, species, location, days, photo, notes)
                    } else {
                        vm.updatePlant(editId, name, species, location, days, photo, notes)
                    }
                    nav.popBackStack()
                },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LeafGreen,
                    disabledContainerColor = Sage.copy(alpha = 0.4f)
                )
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (editId == null) "Agregar al jardín" else "Guardar cambios")
            }
        }
    }

    if (showPicker) {
        SpeciesPickerDialog(
            title = "Elegir especie",
            initialQuery = species,
            onDismiss = { showPicker = false },
            onPick = { picked ->
                applySpecies(picked)
                showPicker = false
            }
        )
    }
}

@Composable
private fun AppField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Muted) },
        minLines = minLines,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Paper,
            unfocusedContainerColor = Paper,
            focusedBorderColor = LeafGreen,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = LeafGreen,
            cursorColor = LeafGreen
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CircleAction(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(SoftMint)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = LeafGreen)
    }
}

@Composable
private fun DayChip(label: String, selected: Boolean, onClick: () -> Unit) {
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
