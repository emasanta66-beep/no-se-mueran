package com.emanu.nosemueran.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emanu.nosemueran.ui.MainViewModel
import com.emanu.nosemueran.ui.components.SectionLabel
import com.emanu.nosemueran.ui.theme.LeafDark
import com.emanu.nosemueran.ui.theme.LeafGreen
import com.emanu.nosemueran.ui.theme.Muted
import com.emanu.nosemueran.ui.theme.Paper
import com.emanu.nosemueran.ui.theme.Sage
import com.emanu.nosemueran.ui.theme.SoftMint
import com.emanu.nosemueran.update.UpdateState
import com.emanu.nosemueran.update.UpdateViewModel
import com.emanu.nosemueran.update.Updater

@Composable
fun SettingsScreen(vm: MainViewModel, autoCheck: Boolean = false) {
    val enabled by vm.notifications.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val updateVm: UpdateViewModel = viewModel()
    val updateState by updateVm.state.collectAsStateWithLifecycle()

    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0.1"
    }

    // Al entrar desde el aviso de actualización, buscamos una vez.
    LaunchedEffect(Unit) {
        if (autoCheck && updateVm.state.value is UpdateState.Idle) updateVm.check()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text("Ajustes", style = MaterialTheme.typography.displaySmall, color = LeafDark)
                Spacer(Modifier.height(2.dp))
                Text(
                    "Todo queda en tu teléfono",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Paper),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Recordatorios de riego",
                            style = MaterialTheme.typography.titleMedium,
                            color = LeafDark
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Un aviso al día, a las 9, si alguna planta necesita agua.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Switch(
                        checked = enabled,
                        onCheckedChange = { vm.toggleNotifications(it) },
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
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Paper),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Actualizaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = LeafDark
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Versión instalada: $versionName",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted
                    )
                    Spacer(Modifier.height(10.dp))

                    when (val state = updateState) {
                        UpdateState.Idle -> {
                            Text(
                                "La app solo se conecta a GitHub cuando pedís buscar una actualización.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Muted
                            )
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = { updateVm.check() }) {
                                Text("Buscar actualización")
                            }
                        }

                        UpdateState.Checking -> Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = LeafGreen
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Buscando en GitHub…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LeafDark
                            )
                        }

                        UpdateState.UpToDate -> Column {
                            Text(
                                "Ya tenés la última versión. 🌿",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LeafDark
                            )
                            TextButton(onClick = { updateVm.check() }) {
                                Text("Buscar de nuevo")
                            }
                        }

                        is UpdateState.Available -> Column {
                            Text(
                                "Hay una versión nueva: ${state.info.versionName}",
                                style = MaterialTheme.typography.titleSmall,
                                color = LeafDark
                            )
                            if (state.info.notes.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    state.info.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Muted,
                                    maxLines = 6,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            Button(onClick = { updateVm.download(state.info) }) {
                                Text("Descargar")
                            }
                        }

                        is UpdateState.Downloading -> Column {
                            Text(
                                "Descargando… ${state.progress}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LeafDark
                            )
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { state.progress / 100f },
                                modifier = Modifier.fillMaxWidth(),
                                color = LeafGreen
                            )
                        }

                        is UpdateState.Downloaded -> Column {
                            Text(
                                "Descarga lista. Tocá Instalar y confirmá.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LeafDark
                            )
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (!Updater.canInstallPackages(context)) {
                                        Toast.makeText(
                                            context,
                                            "Activá \"Instalar apps desconocidas\" y volvé a tocar Instalar.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        Updater.openInstallPermission(context)
                                    } else {
                                        runCatching { Updater.install(context, state.file) }
                                            .onFailure {
                                                Toast.makeText(
                                                    context,
                                                    "No pude abrir el instalador.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                }
                            ) {
                                Text("Instalar")
                            }
                            TextButton(onClick = { updateVm.reset() }) {
                                Text("Cancelar")
                            }
                        }

                        is UpdateState.Failed -> Column {
                            Text(
                                state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            TextButton(onClick = { updateVm.check() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SoftMint,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Tus datos quedan acá", style = MaterialTheme.typography.titleMedium, color = LeafDark)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Plantas, fotos y notas se guardan solo en este teléfono. La única conexión a internet es para buscar actualizaciones cuando vos lo pedís.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LeafDark
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Paper),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    SectionLabel("Cómo funciona el diagnóstico")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "La app mide los colores de la foto (verde, amarillo, marrón y zonas oscuras) y los cruza con tus respuestas. Es una ayuda para mirar mejor, no un diagnóstico profesional.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LeafDark
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Paper),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("No se mueran 🌱", style = MaterialTheme.typography.titleMedium, color = LeafDark)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Versión $versionName · Hecha para que tus plantas vivan un poco mejor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Sage
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Si una planta no mejora, siempre conviene consultar a alguien que sepa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted
                    )
                }
            }
        }

        item {
            Text(
                "Con cariño, para tu jardín. 💚",
                style = MaterialTheme.typography.bodySmall,
                color = Muted,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
