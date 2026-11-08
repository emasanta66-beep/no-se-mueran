package com.emanu.nosemueran.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.emanu.nosemueran.ui.theme.Clay
import com.emanu.nosemueran.ui.theme.LeafDark
import com.emanu.nosemueran.ui.theme.Muted
import com.emanu.nosemueran.ui.theme.Paper
import com.emanu.nosemueran.ui.theme.Sage
import com.emanu.nosemueran.ui.theme.SoftMint
import com.emanu.nosemueran.util.PhotoStore
import java.io.File

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Sage,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
fun InfoChip(
    text: String,
    icon: ImageVector? = null,
    highlight: Boolean = false
) {
    Surface(
        shape = CircleShape,
        color = if (highlight) Clay.copy(alpha = 0.14f) else SoftMint,
        contentColor = if (highlight) Clay else LeafDark
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(5.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatPill(text: String, icon: ImageVector, highlight: Boolean = false) {
    Surface(
        shape = CircleShape,
        color = if (highlight) Clay.copy(alpha = 0.14f) else Paper,
        contentColor = if (highlight) Clay else LeafDark
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (highlight) Clay else Sage)
            Spacer(Modifier.width(7.dp))
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun PlantPhoto(
    path: String?,
    emoji: String,
    modifier: Modifier = Modifier,
    corner: Dp = 0.dp,
    fontSize: TextUnit = 56.sp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(corner))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFE7EFE1), Color(0xFFD2E1CD))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!path.isNullOrBlank() && File(path).exists()) {
            AsyncImage(
                model = File(path),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(text = emoji, fontSize = fontSize)
        }
    }
}

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (onBack != null) 8.dp else 24.dp,
                end = 24.dp,
                top = 12.dp,
                bottom = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = LeafDark
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineMedium, color = LeafDark)
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Muted)
            }
        }
    }
}

class PhotoPickerActions(
    val takePhoto: () -> Unit,
    val pickFromGallery: () -> Unit
)

@Composable
fun rememberPhotoPicker(onPhoto: (String) -> Unit): PhotoPickerActions {
    val context = LocalContext.current
    val callback by rememberUpdatedState(onPhoto)
    var pendingFile by remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val file = pendingFile
        if (success && file != null && file.exists() && file.length() > 0) {
            callback(file.absolutePath)
        } else {
            file?.let { runCatching { it.delete() } }
        }
        pendingFile = null
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = PhotoStore.importFromGallery(context, uri)
            if (path != null) {
                callback(path)
            } else {
                Toast.makeText(context, "No pude importar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    return PhotoPickerActions(
        takePhoto = {
            runCatching {
                val file = PhotoStore.newFile(context)
                pendingFile = file
                cameraLauncher.launch(PhotoStore.uriFor(context, file))
            }.onFailure {
                Toast.makeText(context, "No encontré una app de cámara", Toast.LENGTH_SHORT).show()
            }
        },
        pickFromGallery = { galleryLauncher.launch("image/*") }
    )
}
