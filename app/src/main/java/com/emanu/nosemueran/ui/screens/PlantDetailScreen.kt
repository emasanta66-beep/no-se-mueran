package com.emanu.nosemueran.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.emanu.nosemueran.data.DiaryEntry
import com.emanu.nosemueran.data.SpeciesCatalog
import com.emanu.nosemueran.ui.MainViewModel
import com.emanu.nosemueran.ui.components.InfoChip
import com.emanu.nosemueran.ui.components.PlantPhoto
import com.emanu.nosemueran.ui.components.SectionLabel
import com.emanu.nosemueran.ui.components.SpeciesCareCard
import com.emanu.nosemueran.ui.components.rememberPhotoPicker
import com.emanu.nosemueran.ui.dayNumber
import com.emanu.nosemueran.ui.formatDate
import com.emanu.nosemueran.ui.monthShort
import com.emanu.nosemueran.ui.theme.Clay
import com.emanu.nosemueran.ui.theme.LeafDark
import com.emanu.nosemueran.ui.theme.LeafGreen
import com.emanu.nosemueran.ui.theme.Muted
import com.emanu.nosemueran.ui.theme.Paper
import com.emanu.nosemueran.ui.theme.Sage
import com.emanu.nosemueran.ui.theme.SoftMint
import com.emanu.nosemueran.ui.wateringStatus
import java.io.File

@Composable
fun PlantDetailScreen(vm: MainViewModel, nav: NavController, plantId: String) {
    val plants by vm.plants.collectAsStateWithLifecycle()
    val diary by vm.diary.collectAsStateWithLifecycle()
    val plant = plants.firstOrNull { it.id == plantId }
    val context = LocalContext.current

    var showDelete by remember { mutableStateOf(false) }
    var showNote by remember { mutableStateOf(false) }

    if (plant == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Esta planta ya no está en tu jardín.", color = Muted)
        }
        return
    }

    val entries = remember(diary, plantId) {
        diary.filter { it.plantId == plantId }.sortedByDescending { it.date }
    }
    val species = remember(plant.species) { SpeciesCatalog.find(plant.species) }
    val due = plant.isDue()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            PlantPhoto(
                path = plant.photoPath,
                emoji = SpeciesCatalog.emojiFor(plant.species),
                modifier = Modifier.fillMaxSize(),
                fontSize = 96.sp
            )
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.22f))
                        )
                    )
            )
            IconButton(
                onClick = { nav.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .background(Paper.copy(alpha = 0.92f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = LeafDark)
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { nav.navigate("editar/${plant.id}") },
                    modifier = Modifier.background(Paper.copy(alpha = 0.92f), CircleShape)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = LeafDark)
                }
                IconButton(
                    onClick = { showDelete = true },
                    modifier = Modifier.background(Paper.copy(alpha = 0.92f), CircleShape)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Borrar", tint = Clay)
                }
            }
        }

        Column(Modifier.padding(20.dp)) {
            Text(plant.name, style = MaterialTheme.typography.displaySmall, color = LeafDark)
            if (plant.species.isNotBlank()) {
                Text(
                    plant.species,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = Muted
                )
            }
            if (species != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "☀ ${species.light}  ·  ${species.humidity} humedad",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }

            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip("Cada ${plant.wateringDays} días", Icons.Filled.WaterDrop)
                if (plant.location.isNotBlank()) {
                    InfoChip(plant.location, Icons.Filled.Place)
                }
            }

            Spacer(Modifier.height(20.dp))
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (due) Clay.copy(alpha = 0.11f) else SoftMint,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(if (due) Clay else LeafGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.WaterDrop,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                if (due) "RIEGO PENDIENTE" else "PRÓXIMO RIEGO",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (due) Clay else Sage
                            )
                            Text(
                                wateringStatus(plant),
                                style = MaterialTheme.typography.titleSmall,
                                color = LeafDark
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (plant.lastWatered <= 0L) "Sin riegos registrados"
                        else "Último riego: ${formatDate(plant.lastWatered)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted,
                        modifier = Modifier.padding(start = 54.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = {
                            vm.waterPlant(plant.id)
                            Toast.makeText(context, "¡Registrado! 🌱", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (due) Clay else LeafGreen
                        )
                    ) {
                        Text("Ya la regué")
                    }
                }
            }

            if (species != null) {
                Spacer(Modifier.height(20.dp))
                SpeciesCareCard(
                    species = species!!,
                    onOpenDetail = {
                        nav.navigate("especie/${Uri.encode(species!!.name)}")
                    }
                )
            }

            if (plant.notes.isNotBlank()) {
                Spacer(Modifier.height(20.dp))
                SectionLabel("Notas")
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Paper,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text(
                        plant.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LeafDark,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel("Diario", Modifier.weight(1f))
                TextButton(onClick = { showNote = true }) {
                    Text("+ Agregar", color = LeafGreen)
                }
            }

            if (entries.isEmpty()) {
                Text(
                    "Todavía no hay registros. Contame cómo va creciendo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                entries.forEach { entry ->
                    DiaryCard(entry, onDelete = { vm.deleteDiary(entry.id) })
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("¿Borrar ${plant.name}?") },
            text = { Text("Se van a borrar también sus fotos y todo su diario. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDelete = false
                    vm.deletePlant(plant.id)
                    nav.popBackStack()
                }) { Text("Borrar", color = Clay) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text("Cancelar") }
            }
        )
    }

    if (showNote) {
        var note by remember { mutableStateOf("") }
        var photo by remember { mutableStateOf<String?>(null) }
        val picker = rememberPhotoPicker { path ->
            // Si reemplaza la foto del registro, la anterior no queda huérfana.
            vm.discardPhoto(photo)
            photo = path
        }

        AlertDialog(
            onDismissRequest = {
                vm.discardPhoto(photo)
                showNote = false
            },
            title = { Text("Nuevo registro") },
            text = {
                Column {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("¿Cómo está hoy? ¿La trasplantaste?") },
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = picker.takePhoto) {
                            Text("Cámara", color = LeafGreen)
                        }
                        TextButton(onClick = picker.pickFromGallery) {
                            Text("Galería", color = LeafGreen)
                        }
                        if (photo != null) {
                            TextButton(onClick = {
                                vm.discardPhoto(photo)
                                photo = null
                            }) {
                                Text("Quitar foto", color = Clay)
                            }
                        }
                    }
                    if (photo != null) {
                        AsyncImage(
                            model = File(photo!!),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(SoftMint, RoundedCornerShape(14.dp))
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = note.isNotBlank() || photo != null,
                    onClick = {
                        vm.addDiary(plant.id, note, photo)
                        showNote = false
                    }
                ) { Text("Guardar", color = LeafGreen) }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.discardPhoto(photo)
                    showNote = false
                }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun DiaryCard(entry: DiaryEntry, onDelete: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Paper,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(12.dp)) {
            Column(
                modifier = Modifier.width(44.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    dayNumber(entry.date),
                    style = MaterialTheme.typography.titleLarge,
                    color = LeafGreen
                )
                Text(
                    monthShort(entry.date),
                    style = MaterialTheme.typography.labelSmall,
                    color = Muted
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                if (entry.note.isNotBlank()) {
                    Text(
                        entry.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LeafDark
                    )
                }
                if (entry.photoPath != null) {
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = File(entry.photoPath),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .background(SoftMint, RoundedCornerShape(14.dp))
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Borrar registro",
                    tint = Muted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
