package com.emanu.nosemueran.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.emanu.nosemueran.data.Species
import com.emanu.nosemueran.data.SpeciesCatalog
import com.emanu.nosemueran.ui.components.InfoChip
import com.emanu.nosemueran.ui.components.SectionLabel
import com.emanu.nosemueran.ui.components.SpeciesPickerDialog
import com.emanu.nosemueran.ui.components.rememberPhotoPicker
import com.emanu.nosemueran.ui.theme.LeafDark
import com.emanu.nosemueran.ui.theme.LeafGreen
import com.emanu.nosemueran.ui.theme.Muted
import com.emanu.nosemueran.ui.theme.Paper
import com.emanu.nosemueran.ui.theme.Sage
import com.emanu.nosemueran.ui.theme.SoftMint
import com.emanu.nosemueran.util.DiagnoseAnswers
import com.emanu.nosemueran.util.ImageAnalysis
import com.emanu.nosemueran.util.PhotoAnalysis
import com.emanu.nosemueran.util.PhotoStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

@Composable
fun DiagnoseScreen() {
    var photoPath by remember { mutableStateOf<String?>(null) }
    var analysis by remember { mutableStateOf<PhotoAnalysis?>(null) }
    var analyzing by remember { mutableStateOf(false) }
    var analyzed by remember { mutableStateOf(false) }
    var species by remember { mutableStateOf<Species?>(null) }
    var showPicker by remember { mutableStateOf(false) }

    var soilDry by remember { mutableStateOf(false) }
    var overwatered by remember { mutableStateOf(false) }
    var yellowLeaves by remember { mutableStateOf(false) }
    var brownSpots by remember { mutableStateOf(false) }
    var directSun by remember { mutableStateOf(false) }
    var deformedLeaves by remember { mutableStateOf(false) }
    var bugs by remember { mutableStateOf(false) }
    var whitePowder by remember { mutableStateOf(false) }
    var softStem by remember { mutableStateOf(false) }
    var cold by remember { mutableStateOf(false) }
    var recentlyTransplanted by remember { mutableStateOf(false) }
    var noDrainage by remember { mutableStateOf(false) }
    var fertilized by remember { mutableStateOf(false) }
    var holesInLeaves by remember { mutableStateOf(false) }
    var spottedFruit by remember { mutableStateOf(false) }

    fun resetAnswers() {
        soilDry = false
        overwatered = false
        yellowLeaves = false
        brownSpots = false
        directSun = false
        deformedLeaves = false
        bugs = false
        whitePowder = false
        softStem = false
        cold = false
        recentlyTransplanted = false
        noDrainage = false
        fertilized = false
        holesInLeaves = false
        spottedFruit = false
    }

    val picker = rememberPhotoPicker { path ->
        // La foto del diagnóstico es temporal: al reemplazarla, borramos la anterior.
        PhotoStore.delete(photoPath)
        photoPath = path
        analysis = null
        analyzed = false
        resetAnswers()
    }

    // Al salir de la pantalla borramos la foto temporal del análisis.
    val currentPhoto by rememberUpdatedState(photoPath)
    DisposableEffect(Unit) {
        onDispose { PhotoStore.delete(currentPhoto) }
    }

    LaunchedEffect(photoPath) {
        val path = photoPath ?: return@LaunchedEffect
        analyzing = true
        analyzed = false
        analysis = null
        analysis = withContext(Dispatchers.Default) { ImageAnalysis.analyzeFile(path) }
        analyzing = false
        analyzed = true
    }

    val answers = DiagnoseAnswers(
        soilDry = soilDry,
        overwatered = overwatered,
        yellowLeaves = yellowLeaves,
        brownSpots = brownSpots,
        directSun = directSun,
        deformedLeaves = deformedLeaves,
        bugs = bugs,
        whitePowder = whitePowder,
        softStem = softStem,
        cold = cold,
        recentlyTransplanted = recentlyTransplanted,
        noDrainage = noDrainage,
        fertilized = fertilized,
        holesInLeaves = holesInLeaves,
        spottedFruit = spottedFruit
    )

    val diagnosis = remember(analysis, answers, species) {
        ImageAnalysis.diagnose(analysis?.leaf, answers, species)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Diagnóstico", style = MaterialTheme.typography.displaySmall, color = LeafDark)
                Spacer(Modifier.height(2.dp))
                Text(
                    "Una segunda opinión para tus hojas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Paper),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(SoftMint),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoPath != null) {
                            AsyncImage(
                                model = File(photoPath!!),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔎", fontSize = 44.sp)
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Sacá una foto de la planta",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LeafDark
                                )
                                Text(
                                    "Con luz natural y fondo simple",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Muted
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = picker.takePhoto,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LeafGreen)
                        ) {
                            Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Cámara")
                        }
                        OutlinedButton(
                            onClick = picker.pickFromGallery,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Galería")
                        }
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Paper,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    SectionLabel("¿Qué planta es?")
                    Spacer(Modifier.height(8.dp))
                    if (species == null) {
                        Text(
                            "Elegirla ayuda a darte consejos más precisos. Es opcional.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showPicker = true },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Filled.LocalFlorist, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Elegir especie")
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            InfoChip("${species!!.emoji} ${species!!.name}")
                            Spacer(Modifier.width(6.dp))
                            TextButton(onClick = { species = null }) {
                                Text("Quitar", color = Muted)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${species!!.light} · riego cada ${species!!.wateringDays} días · ${species!!.difficulty}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                    }
                }
            }
        }

        if (analyzing) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(color = LeafGreen, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Mirando las hojas...", style = MaterialTheme.typography.bodyMedium, color = Muted)
                }
            }
        }

        if (analyzed && analysis != null) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Paper),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        SectionLabel("Lo que veo en la foto")
                        Spacer(Modifier.height(12.dp))
                        val leaf = analysis!!.leaf
                        StatBar("Verde sano", leaf.green, Color(0xFF6B9B6E))
                        Spacer(Modifier.height(10.dp))
                        StatBar("Amarillo", leaf.yellow, Color(0xFFDDB33F))
                        Spacer(Modifier.height(10.dp))
                        StatBar("Marrón", leaf.brown, Color(0xFFA9713F))
                        Spacer(Modifier.height(10.dp))
                        StatBar("Zonas oscuras", leaf.dark, Color(0xFF55554E))
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Predominan ${ImageAnalysis.summary(leaf)}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                    }
                }
            }
        }

        if (analyzed && analysis == null) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFBF1DA),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "No pude leer bien esta foto",
                            style = MaterialTheme.typography.titleSmall,
                            color = LeafDark
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Probá con más luz, acercate a las hojas y evitá fondos muy blancos o muy oscuros. " +
                                "Igual podés responder las preguntas y ver la recomendación.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LeafDark
                        )
                    }
                }
            }
        }

        if (analyzed) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Paper),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        SectionLabel("Contame un poco más")
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Marcá todo lo que veas. Cuantas más respuestas, mejor la recomendación.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )

                        Spacer(Modifier.height(12.dp))
                        SectionLabel("Riego y tierra")
                        QuestionSwitch("La tierra está seca", soilDry) { soilDry = it }
                        QuestionSwitch("Regué de más esta semana", overwatered) { overwatered = it }
                        QuestionSwitch("La maceta no tiene drenaje", noDrainage) { noDrainage = it }
                        QuestionSwitch("La trasplanté hace poco", recentlyTransplanted) { recentlyTransplanted = it }
                        QuestionSwitch("Usé fertilizante hace poco", fertilized) { fertilized = it }

                        Spacer(Modifier.height(12.dp))
                        SectionLabel("Hojas")
                        QuestionSwitch("Veo hojas amarillas", yellowLeaves) { yellowLeaves = it }
                        QuestionSwitch("Veo manchas marrones", brownSpots) { brownSpots = it }
                        QuestionSwitch("Hojas nuevas deformadas o rizadas", deformedLeaves) { deformedLeaves = it }
                        QuestionSwitch("Hojas con agujeros", holesInLeaves) { holesInLeaves = it }
                        QuestionSwitch("Polvo blanco o gris sobre las hojas", whitePowder) { whitePowder = it }

                        Spacer(Modifier.height(12.dp))
                        SectionLabel("Bichos y ambiente")
                        QuestionSwitch("Veo bichitos (pulgones, mosquitas)", bugs) { bugs = it }
                        QuestionSwitch("Tallos blandos u oscuros", softStem) { softStem = it }
                        QuestionSwitch("Pasó frío o helada", cold) { cold = it }
                        QuestionSwitch("Está en sol directo", directSun) { directSun = it }
                        QuestionSwitch("Los frutos tienen manchas", spottedFruit) { spottedFruit = it }
                    }
                }
            }

            item {
                val background = when (diagnosis.level) {
                    0 -> SoftMint
                    1 -> Color(0xFFFBF1DA)
                    else -> Color(0xFFF7E1D7)
                }
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                when (diagnosis.level) {
                                    0 -> "🌿"
                                    1 -> "⚠️"
                                    else -> "🚨"
                                },
                                fontSize = 26.sp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                diagnosis.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = LeafDark
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            diagnosis.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LeafDark
                        )
                        Spacer(Modifier.height(12.dp))
                        SectionLabel("Qué hacer")
                        Spacer(Modifier.height(6.dp))
                        diagnosis.tips.forEach { tip ->
                            Row(Modifier.padding(bottom = 6.dp)) {
                                Text("•", color = Sage, style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    tip,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LeafDark
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Es una estimación orientativa hecha en tu teléfono, sin internet. " +
                        "Si la planta empeora, consultá con alguien de confianza o un vivero.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
        }

        if (photoPath == null) {
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Paper,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        SectionLabel("Cómo funciona")
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "La app analiza los colores de la foto: cuánto verde, amarillo, marrón y zonas oscuras hay en la planta. " +
                                "Después cruza eso con tus respuestas y, si elegís la especie, con sus cuidados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LeafDark
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No reconoce la especie con certeza: podés elegirla vos para afinar el consejo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                    }
                }
            }
        }
    }

    if (showPicker) {
        SpeciesPickerDialog(
            title = "¿Qué planta es?",
            onDismiss = { showPicker = false },
            onPick = {
                species = it
                showPicker = false
            }
        )
    }
}

@Composable
private fun StatBar(label: String, value: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = LeafDark,
            modifier = Modifier.width(104.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((value / 100f).coerceIn(0f, 1f))
                    .background(color, CircleShape)
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            "${value.roundToInt()}%",
            style = MaterialTheme.typography.labelMedium,
            color = Muted,
            modifier = Modifier.width(38.dp)
        )
    }
}

@Composable
private fun QuestionSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = LeafDark,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = LeafGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD3C4),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
