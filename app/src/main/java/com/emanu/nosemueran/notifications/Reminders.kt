package com.emanu.nosemueran.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object Reminders {

    private const val WORK_NAME = "riego-diario"
    private const val PREFS = "nosemueran"
    private const val KEY_VERSION = "recordatorio_version"
    private const val VERSION = 2

    fun schedule(context: Context) {
        // Un período de 24 h mantiene el aviso cerca de la misma hora.
        // El delay inicial solo aplica a la primera ejecución.
        val request = PeriodicWorkRequestBuilder<WateringWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(millisUntilNextNine(), TimeUnit.MILLISECONDS)
            .build()

        // La primera vez tras actualizar la app hay que descartar el trabajo
        // viejo de 12 h, que dejaría el aviso a otra hora. Después alcanza
        // con UPDATE para no perder la programación existente.
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val version = prefs.getInt(KEY_VERSION, 0)
        val policy = if (version >= VERSION) {
            ExistingPeriodicWorkPolicy.UPDATE
        } else {
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
        }

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            policy,
            request
        )
        if (version < VERSION) {
            prefs.edit().putInt(KEY_VERSION, VERSION).apply()
        }
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun millisUntilNextNine(): Long {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(9, 0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next).toMillis()
    }
}
