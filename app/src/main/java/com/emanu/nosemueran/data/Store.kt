package com.emanu.nosemueran.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Almacenamiento local, sin internet. Todo vive en archivos JSON
 * dentro de filesDir y se expone como StateFlow para Compose.
 *
 * Las escrituras son atómicas (archivo temporal + rename) y se conserva
 * un respaldo .bak de la última versión buena. Si el JSON principal no
 * se puede leer, se intenta el respaldo y, como último recurso, se guarda
 * una copia del archivo dañado para no perderlo.
 */
object Store {

    @Volatile
    private var initialized = false

    private lateinit var plantsFile: File
    private lateinit var diaryFile: File
    private lateinit var prefs: SharedPreferences

    /** Serializa las mutaciones y las escrituras a disco. */
    private val lock = Any()

    private val _plants = MutableStateFlow<List<Plant>>(emptyList())
    val plants: StateFlow<List<Plant>> = _plants

    private val _diary = MutableStateFlow<List<DiaryEntry>>(emptyList())
    val diary: StateFlow<List<DiaryEntry>> = _diary

    private val _notifications = MutableStateFlow(true)
    val notifications: StateFlow<Boolean> = _notifications

    fun init(context: Context) {
        // Mismo monitor que las mutaciones: así no se puede leer el disco
        // mientras otra operación modifica las listas en memoria.
        synchronized(lock) {
            if (initialized) return
            plantsFile = File(context.filesDir, "plantas.json")
            diaryFile = File(context.filesDir, "diario.json")
            prefs = context.getSharedPreferences("nosemueran", Context.MODE_PRIVATE)
            _notifications.value = prefs.getBoolean("notificaciones", true)
            _plants.value = readPlants()
            _diary.value = readDiary()
            // Se marca al final: si algo falla, otro init puede reintentar.
            initialized = true
        }
    }

    fun addPlant(plant: Plant) {
        synchronized(lock) {
            _plants.value = _plants.value + plant
            persistPlants()
        }
    }

    fun updatePlant(plant: Plant) {
        synchronized(lock) {
            _plants.value = _plants.value.map { if (it.id == plant.id) plant else it }
            persistPlants()
        }
    }

    fun deletePlant(id: String) {
        synchronized(lock) {
            val plant = _plants.value.firstOrNull { it.id == id }
            val diaryPhotos = _diary.value
                .filter { it.plantId == id }
                .mapNotNull { it.photoPath }
            _plants.value = _plants.value.filterNot { it.id == id }
            _diary.value = _diary.value.filterNot { it.plantId == id }
            persistPlants()
            persistDiary()
            plant?.photoPath?.let { runCatching { File(it).delete() } }
            diaryPhotos.forEach { runCatching { File(it).delete() } }
        }
    }

    fun waterPlant(id: String, whenMillis: Long = System.currentTimeMillis()) {
        synchronized(lock) {
            _plants.value = _plants.value.map {
                if (it.id == id) it.copy(lastWatered = whenMillis) else it
            }
            persistPlants()
        }
    }

    fun addDiaryEntry(entry: DiaryEntry) {
        synchronized(lock) {
            _diary.value = _diary.value + entry
            persistDiary()
        }
    }

    fun deleteDiaryEntry(id: String) {
        synchronized(lock) {
            val entry = _diary.value.firstOrNull { it.id == id }
            _diary.value = _diary.value.filterNot { it.id == id }
            persistDiary()
            entry?.photoPath?.let { runCatching { File(it).delete() } }
        }
    }

    fun setNotifications(enabled: Boolean) {
        synchronized(lock) {
            _notifications.value = enabled
            prefs.edit().putBoolean("notificaciones", enabled).apply()
        }
    }

    fun lastNotifiedDate(): String = prefs.getString("ultimo_aviso", "") ?: ""

    fun setLastNotifiedDate(value: String) {
        prefs.edit().putString("ultimo_aviso", value).apply()
    }

    // ---------- JSON ----------

    private fun persistPlants() {
        val arr = JSONArray()
        _plants.value.forEach { p ->
            arr.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("species", p.species)
                    put("photoPath", p.photoPath ?: JSONObject.NULL)
                    put("location", p.location)
                    put("wateringDays", p.wateringDays)
                    put("lastWatered", p.lastWatered)
                    put("notes", p.notes)
                    put("createdAt", p.createdAt)
                }
            )
        }
        runCatching { writeAtomic(plantsFile, arr.toString()) }
    }

    private fun persistDiary() {
        val arr = JSONArray()
        _diary.value.forEach { e ->
            arr.put(
                JSONObject().apply {
                    put("id", e.id)
                    put("plantId", e.plantId)
                    put("date", e.date)
                    put("note", e.note)
                    put("photoPath", e.photoPath ?: JSONObject.NULL)
                }
            )
        }
        runCatching { writeAtomic(diaryFile, arr.toString()) }
    }

    /**
     * Escribe en un temporal y lo renombra de forma atómica. Antes de
     * reemplazar, conserva la versión anterior en .bak.
     */
    private fun writeAtomic(target: File, content: String, updateBackup: Boolean = true) {
        val dir = target.parentFile ?: return
        val tmp = File(dir, target.name + ".tmp")
        tmp.writeText(content)

        // Al reparar desde el respaldo, el contenido actual del principal es
        // basura: no debe pisar el .bak que justamente estamos usando.
        if (updateBackup && target.exists() && target.length() > 0L) {
            runCatching {
                target.copyTo(File(dir, target.name + ".bak"), overwrite = true)
            }
        }

        val moved = runCatching {
            Files.move(
                tmp.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        }.isSuccess

        if (!moved) {
            runCatching {
                Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }.onFailure {
                target.writeText(content)
                tmp.delete()
            }
        }
    }

    private fun readPlants(): List<Plant> = readList(plantsFile) { arr ->
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            Plant(
                id = o.getString("id"),
                name = o.getString("name"),
                species = o.optString("species", ""),
                photoPath = if (o.isNull("photoPath")) null else o.getString("photoPath"),
                location = o.optString("location", ""),
                wateringDays = o.optInt("wateringDays", 7),
                lastWatered = o.optLong("lastWatered", 0L),
                notes = o.optString("notes", ""),
                createdAt = o.optLong("createdAt", 0L)
            )
        }
    }

    private fun readDiary(): List<DiaryEntry> = readList(diaryFile) { arr ->
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            DiaryEntry(
                id = o.getString("id"),
                plantId = o.getString("plantId"),
                date = o.optLong("date", 0L),
                note = o.optString("note", ""),
                photoPath = if (o.isNull("photoPath")) null else o.getString("photoPath")
            )
        }
    }

    private fun <T> readList(file: File, parse: (JSONArray) -> List<T>): List<T> {
        if (!file.exists()) return emptyList()

        runCatching { parse(JSONArray(file.readText())) }.getOrNull()?.let { return it }

        val backup = File(file.parentFile, file.name + ".bak")
        if (backup.exists()) {
            val fromBackup = runCatching { parse(JSONArray(backup.readText())) }.getOrNull()
            if (fromBackup != null) {
                // Guardamos el principal dañado y lo reemplazamos por el respaldo,
                // sin tocar el .bak (writeAtomic con updateBackup = false).
                runCatching {
                    file.copyTo(
                        File(file.parentFile, "${file.name}.corrupto-${System.currentTimeMillis()}"),
                        overwrite = true
                    )
                }
                runCatching { writeAtomic(file, backup.readText(), updateBackup = false) }
                return fromBackup
            }
        }

        // No se pudo leer ni el principal ni el respaldo: conservamos una copia
        // del archivo dañado antes de que cualquier guardado lo sobrescriba.
        runCatching {
            file.copyTo(
                File(file.parentFile, "${file.name}.corrupto-${System.currentTimeMillis()}"),
                overwrite = true
            )
        }
        return emptyList()
    }
}
