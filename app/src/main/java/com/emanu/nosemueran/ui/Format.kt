package com.emanu.nosemueran.ui

import com.emanu.nosemueran.data.Plant
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val locale = Locale("es", "AR")

private val longDate = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", locale)
private val shortDate = DateTimeFormatter.ofPattern("d MMM yyyy", locale)

fun capitalizeFirst(text: String): String =
    text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

fun todayLabel(): String =
    capitalizeFirst(Instant.now().atZone(ZoneId.systemDefault()).toLocalDate().format(longDate))

fun formatDate(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().format(shortDate)

fun dayNumber(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).dayOfMonth.toString()

fun monthShort(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .month
        .getDisplayName(TextStyle.SHORT, locale)
        .uppercase(locale)
        .removeSuffix(".")

fun nextWateringLabel(plant: Plant): String {
    val days = plant.daysUntilWatering()
    return when {
        days < 0 -> "Atrasada ${-days} d"
        days == 0L -> "Toca regar"
        days == 1L -> "Mañana"
        else -> "En $days días"
    }
}

fun wateringStatus(plant: Plant): String {
    val days = plant.daysUntilWatering()
    return when {
        days < 0 -> "Se pasó ${-days} día${if (days == -1L) "" else "s"}. Dale agua hoy."
        days == 0L -> "Hoy le toca agua."
        days == 1L -> "Mañana le toca agua."
        else -> "Faltan $days días para el próximo riego."
    }
}
