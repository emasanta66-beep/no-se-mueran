package com.emanu.nosemueran.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.emanu.nosemueran.data.Store
import java.time.LocalDate

/** Revisa una vez por día si hay plantas con riego pendiente. */
class WateringWorker(
    appContext: Context,
    params: WorkerParameters
) : Worker(appContext, params) {

    override fun doWork(): Result {
        val context = applicationContext
        Store.init(context)

        if (!Store.notifications.value) return Result.success()

        val due = Store.plants.value.filter { it.isDue() }
        if (due.isEmpty()) return Result.success()

        val today = LocalDate.now().toString()
        if (Store.lastNotifiedDate() == today) return Result.success()

        Notifications.ensureChannel(context)
        // Solo marcamos el día si el aviso realmente se publicó: si el permiso
        // falta o las notificaciones están apagadas, se reintenta mañana.
        if (Notifications.notifyDue(context, due)) {
            Store.setLastNotifiedDate(today)
        }
        return Result.success()
    }
}
