package com.emanu.nosemueran.data

import java.util.UUID

data class Plant(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val species: String = "",
    val photoPath: String? = null,
    val location: String = "",
    val wateringDays: Int = 7,
    val lastWatered: Long = System.currentTimeMillis(),
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val nextWatering: Long
        get() = lastWatered + wateringDays * DAY_MS

    fun daysUntilWatering(now: Long = System.currentTimeMillis()): Long =
        // lastWatered <= 0 significa "nunca regada": cuenta como pendiente.
        if (lastWatered <= 0L) 0L
        else Math.ceil((nextWatering - now).toDouble() / DAY_MS).toLong()

    fun isDue(now: Long = System.currentTimeMillis()): Boolean = daysUntilWatering(now) <= 0

    companion object {
        const val DAY_MS = 86_400_000L
    }
}

data class DiaryEntry(
    val id: String = UUID.randomUUID().toString(),
    val plantId: String,
    val date: Long = System.currentTimeMillis(),
    val note: String,
    val photoPath: String? = null
)

data class Prefill(val speciesName: String, val wateringDays: Int)
