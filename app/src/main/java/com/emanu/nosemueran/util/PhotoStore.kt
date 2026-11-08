package com.emanu.nosemueran.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object PhotoStore {

    fun newFile(context: Context): File {
        val dir = File(context.filesDir, "fotos").apply { mkdirs() }
        val name = "foto_${System.currentTimeMillis()}_${(100..999).random()}.jpg"
        return File(dir, name)
    }

    fun uriFor(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    /** Copia una imagen elegida de la galería al almacenamiento privado de la app. */
    fun importFromGallery(context: Context, uri: Uri): String? {
        // Abrimos el origen antes de crear el destino: si la URI es inválida
        // no queda un archivo vacío huérfano en fotos/.
        val input = runCatching { context.contentResolver.openInputStream(uri) }
            .getOrNull() ?: return null
        val dest = newFile(context)
        return try {
            input.use { source ->
                dest.outputStream().use { output -> source.copyTo(output) }
            }
            if (dest.length() > 0L) {
                dest.absolutePath
            } else {
                dest.delete()
                null
            }
        } catch (t: Throwable) {
            dest.delete()
            null
        }
    }

    fun delete(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching { File(path).delete() }
    }
}
