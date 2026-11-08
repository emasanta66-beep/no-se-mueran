package com.emanu.nosemueran.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.emanu.nosemueran.data.DiaryEntry
import com.emanu.nosemueran.data.Plant
import com.emanu.nosemueran.data.Prefill
import com.emanu.nosemueran.data.Store
import com.emanu.nosemueran.notifications.Notifications
import com.emanu.nosemueran.notifications.Reminders
import com.emanu.nosemueran.util.PhotoStore
import java.io.File

class MainViewModel(app: Application) : AndroidViewModel(app) {

    val plants = Store.plants
    val diary = Store.diary
    val notifications = Store.notifications

    var prefill by mutableStateOf<Prefill?>(null)
        private set

    fun applyPrefill(value: Prefill?) {
        prefill = value
    }

    private fun clean(value: String) = value.trim()

    fun createPlant(
        name: String,
        species: String,
        location: String,
        wateringDays: Int,
        photoPath: String?,
        notes: String
    ): String {
        val plant = Plant(
            name = clean(name),
            species = clean(species),
            location = clean(location),
            wateringDays = wateringDays.coerceIn(1, 90),
            photoPath = photoPath,
            notes = clean(notes)
        )
        Store.addPlant(plant)
        return plant.id
    }

    fun updatePlant(
        id: String,
        name: String,
        species: String,
        location: String,
        wateringDays: Int,
        photoPath: String?,
        notes: String
    ) {
        val current = Store.plants.value.firstOrNull { it.id == id } ?: return
        val oldPhoto = current.photoPath
        Store.updatePlant(
            current.copy(
                name = clean(name),
                species = clean(species),
                location = clean(location),
                wateringDays = wateringDays.coerceIn(1, 90),
                photoPath = photoPath,
                notes = clean(notes)
            )
        )
        // Recién después de guardar la nueva referencia borramos la anterior:
        // si algo falla, la planta nunca queda apuntando a una foto borrada.
        if (oldPhoto != null && oldPhoto != photoPath) {
            PhotoStore.delete(oldPhoto)
        }
    }

    fun deletePlant(id: String) {
        Store.deletePlant(id)
    }

    fun waterPlant(id: String) {
        Store.waterPlant(id)
    }

    fun addDiary(plantId: String, note: String, photoPath: String?) {
        if (note.isBlank() && photoPath == null) return
        Store.addDiaryEntry(
            DiaryEntry(
                plantId = plantId,
                note = clean(note),
                photoPath = photoPath
            )
        )
    }

    fun deleteDiary(id: String) {
        Store.deleteDiaryEntry(id)
    }

    fun toggleNotifications(enabled: Boolean) {
        Store.setNotifications(enabled)
        val context = getApplication<Application>()
        if (enabled) {
            Notifications.ensureChannel(context)
            Reminders.schedule(context)
        } else {
            Reminders.cancel(context)
        }
    }

    /** Borra la foto temporal si el usuario elige otra. */
    fun discardPhoto(path: String?) {
        if (path.isNullOrBlank()) return
        if (Store.plants.value.any { it.photoPath == path }) return
        if (Store.diary.value.any { it.photoPath == path }) return
        runCatching { File(path).delete() }
    }
}
